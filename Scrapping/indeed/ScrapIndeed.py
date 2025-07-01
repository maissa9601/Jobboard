import logging
import re
import time
from bs4 import BeautifulSoup
from concurrent.futures import ThreadPoolExecutor, as_completed
from CloudflareBypasser import CloudflareBypasser

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

UNWANTED_PARTS = [
    "career/", "salaries/", "cmp/", "/pagead/", "/company/", "/job/rc", "/addlLoc/redirect?"
]
#nettoyer une chaine
def clean(text):
    return re.sub(r'\s+', ' ', text).strip() if text else None

#verifier les fausses reirections
def unwanted(url):
    return any(part in url for part in UNWANTED_PARTS)

#extract un texte de tag=span ou div...
def get_text(tag, fallback=None):
    try:
        return clean(tag.get_text()) if tag else fallback
    except Exception as e:
        logging.error({e})
        return fallback
#driver est une fenétre
def job_details(driver):
    url = driver.url
    if unwanted(url):
        logging.warning(f"[IGNORED] : {url}")
        return None

    soup = BeautifulSoup(driver.html, 'html.parser')

    title = None
    try:
        title_tag = soup.find('h2', {'data-testid': 'jobsearch-JobInfoHeader-title'})
        span = title_tag.find('span') if title_tag else None
        title = get_text(span or title_tag)
        if title:
            title = title.replace(" - job post", "").strip()
        if not title:
            fallback = soup.find(['h1', 'h2'])
            title = get_text(fallback)
    except Exception as e:
        logging.error(  {e})

    if not title:
        logging.warning(f"No title for  : {url}")
        return None

    company = get_text(soup.find(attrs={"data-testid": "inlineHeader-companyName"}))
    location = get_text(soup.find(attrs={"data-testid": "inlineHeader-companyLocation"}))
    salary = get_text(soup.find("div", {"aria-label": "Salaire"}).find("span") if soup.find("div", {"aria-label": "Salaire"}) else None)
    contract_type = get_text(soup.find("div", {"aria-label": "Type de poste"}).find("span") if soup.find("div", {"aria-label": "Type de poste"}) else None)
    description_profile = None
    description_div = soup.find("div", id="jobDescriptionText")
    if description_div:
        lines = description_div.get_text(separator='\n', strip=True).split('\n')
        raw_description = '\n'.join(lines[:3])
        description_profile = clean(raw_description)

    experience = None
    for h3 in soup.find_all('h3', class_='jobSectionHeader'):
        if "Niveau d'expérience requis" in h3.get_text():
            text_nodes = [t for t in h3.parent.stripped_strings if t != h3.get_text()]
            experience = clean(text_nodes[0]) if text_nodes else None
            break


    job_details = {
        "title": title,
        "company": company,
        "location": location,
        "salary": salary,
        "contract_type": contract_type,
        "experience": experience,
        "published":None,
        "expires": None,
        "description_profile": description_profile,
        "url": url,
        "source": "Indeed"
    }

    logging.info(f"[EXTRAIT] Offre : {title}")
    return job_details
#extract tous les liens de détails de job  qui existent sur une page
def job_links(driver):
    soup = BeautifulSoup(driver.html, 'html.parser')
    links = set()

    for a in soup.find_all('a', href=True):
        href = a['href']
        if unwanted(href):
            continue
        if 'jk=' in href:
            url = 'https://fr.indeed.com' + href if href.startswith('/') else href
            links.add(url)

    logging.info(f"[LIENS] {len(links)} ")
    return list(links)
#ouvre le lien de détail de offre et extract les détails
def process_job(driver, link):
    try:
        tab = driver.new_tab()
        tab.get(link)
        time.sleep(2)
        job = job_details(tab)
        tab.close()
        return job
    except Exception as e:
        logging.error(f"[THREAD-ERROR] {link} : {e}")
        return None
#execution des taches en paralélle
def multi_thread(driver, links, max_workers=1):
    start = time.time()
    logging.info(f"[MULTI-THREAD] Lanch with {max_workers} threads...")
    results = []
    urls_seen = set()

    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [executor.submit(process_job, driver, link) for link in links]
        for idx, future in enumerate(as_completed(futures), 1):
            job = future.result()
            if job and job["url"] not in urls_seen:
                results.append(job)
                urls_seen.add(job["url"])
    end = time.time()
    print(f">>> Time with  {max_workers} threads : {round(end - start, 2)}s")
    return results

#extract plusieurs pages
def all_pages(driver, start_url, max_pages=5, delay=3):
    all_jobs = []
    current_url = start_url
    page_count = 0

    while current_url and page_count < max_pages:
        logging.info(f"[PAGE {page_count + 1}] loading : {current_url}")
        driver.get(current_url)
        time.sleep(delay)

        links = job_links(driver)
        logging.info(f"[LIENS] {len(links)} ")

        jobs = multi_thread(driver, links, max_workers=20)
        all_jobs.extend(jobs)

        # pagination suivante
        soup = BeautifulSoup(driver.html, 'html.parser')
        next_button = soup.find('a', {'data-testid': 'pagination-page-next'})
        if next_button and next_button.get('href'):
            next_href = next_button['href']
            current_url = "https://fr.indeed.com" + next_href
            page_count += 1
        else:
            logging.info("[FIN] no page found.")
            break

    logging.info(f"[TOTAL] {len(all_jobs)} {page_count + 1} pages.")
    return all_jobs
def scrape_all_cities(driver, villes, max_pages=3):
    all_jobs = []
    for ville in villes:
        logging.info(f" Scraping city : {ville}")
        ville_query = ville.replace(" ", "+")
        start_url = f"https://fr.indeed.com/jobs?q=&l={ville_query}"

        try:
            driver.get(start_url)
            # necessite un bypass cloudflare
            cf_bypasser = CloudflareBypasser(driver)
            cf_bypasser.bypass()
            time.sleep(3)

            jobs = all_pages(driver, start_url, max_pages=max_pages, delay=2)
            all_jobs.extend(jobs)
        except Exception as e:
            logging.error(f"[ERREUR VILLE] {ville} : {e}")
        time.sleep(2)
    return all_jobs



