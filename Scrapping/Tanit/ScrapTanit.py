import time
import logging
import random
from bs4 import BeautifulSoup
from concurrent.futures import ThreadPoolExecutor, as_completed
import re


logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')


#introduire un delai aléatoire
def random_delay(min_delay=3, max_delay=7):
    time.sleep(random.uniform(min_delay, max_delay))


def scrap_detail_page(html_content):
    soup = BeautifulSoup(html_content, 'html.parser')

    def get_dd(label):
        dt = soup.find('dt', text=lambda x: x and label in x)
        if dt:
            dd = dt.find_next_sibling('dd')
            return dd.get_text(strip=True) if dd else ""
        return ""


    def clean_text(text):
       return re.sub(r'\s+', ' ', text).strip()
    contractype = clean_text(get_dd("Type d'emploi désiré"))
    experience = get_dd("Experience")
    salary = ""

    def get_section_text(title):
        header = soup.find('h3', string=lambda x: x and title in x)
        if header:
            div = header.find_next_sibling('div', class_='details-body__content')
            if div:
                return div.get_text(strip=True)
        return ""

    description = get_section_text("Description de l'emploi")
    exigences = get_section_text("Exigences de l'emploi")
    expires = get_section_text("Date d'expiration")

    full_description = description
    if exigences:
        full_description += "\n\nExigences:\n" + exigences

    return {
        "contractype": contractype,
        "experience": experience,
        "salary": salary,
        "expires": expires,
        "description": full_description
    }



def process_job(driver, article):
    try:
        title_el = article.ele('css:div.listing-item__title a')
        title = title_el.text.strip()
        url = title_el.attr('href')

        company = article.ele('css:span.listing-item__info--item-company')
        location = article.ele('css:span.listing-item__info--item-location')
        published = article.ele('css:div.listing-item__date')

        company = company.text.strip() if company else "Entreprise non spécifiée"
        location = location.text.strip() if location else "Localisation non spécifiée"
        published = published.text.strip() if published else ""

        random_delay(7, 10)

        tab = driver.new_tab(url)

        for _ in range(30):
            if tab.ele('css:h1.details-header__title'):
                break
            time.sleep(1)
        else:
            logging.warning(f"no element  : {url}")
            with open('debug_failed_page.html', 'w', encoding='utf-8') as f:
                f.write(tab.html)
            tab.close()
            return None

        html = tab.html
        details = scrap_detail_page(html)

        job = {
            "title": title,
            "contractype": details["contractype"],
            "description": details["description"],
            "company": company,
            "location": location,
            "salary": details["salary"],
            "source": "TanitJobs",
            "experience": details["experience"],
            "published": published,
            "expires": details["expires"],
            "url": url
        }

        logging.info(f"Scraped job: {title}")
        tab.close()
        return job

    except Exception as e:
        logging.error(f"Erreur dans un thread: {e}")
        return None

def scrape_jobs(driver, limit=None):
    logging.info("Scraping listing page...")
    articles = driver.eles('css:article.listing-item')
    logging.info(f"Nombre d'articles trouvés: {len(articles)}")

    jobs = []

    with ThreadPoolExecutor(max_workers=20) as executor:  # 5 threads
        futures = [executor.submit(process_job, driver, article) for article in articles[:limit]]

        for future in as_completed(futures):
            job = future.result()
            if job:
                jobs.append(job)

    logging.info(f"Total jobs scraped: {len(jobs)}")
    return jobs



def scrape_all_pages(driver, pages=5):
    base_url = "https://www.tanitjobs.com/jobs/?page="
    all_jobs = []

    for page_num in range(1, pages + 1):
        url = f"{base_url}{page_num}"
        logging.info(f"\n--- Page {page_num} ---")

        try:
            driver.get(url)

            for _ in range(10):
                if driver.eles('css:article.listing-item'):
                    break
                time.sleep(1)
            else:
                logging.warning(f"no offer{page_num}")
                continue

            jobs = scrape_jobs(driver)
            all_jobs.extend(jobs)
            random_delay()

        except Exception as e:
            logging.error(f"error on page: {page_num} : {e}")

    return all_jobs