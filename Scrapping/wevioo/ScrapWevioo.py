import requests
from bs4 import BeautifulSoup
import re
from concurrent.futures import ThreadPoolExecutor, as_completed
import urllib3

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

base_url = "https://www.wevioo.com"
listing_url = f"{base_url}/fr/offres-d-emploi"

def load_page(url):
    try:
        response = requests.get(url, verify=False)
        response.raise_for_status()
        print(f"success : {url}")
        return response.text
    except requests.RequestException as e:
        print(f"error while scraping {url} : {e}")
        return None

def get_field(soup, field_class):
    field = soup.select_one(f".{field_class} .field--item")
    return field.get_text(strip=True) if field else ""

def extract_experience(soup):
    profil_block = soup.select_one(".field.field--name-field-profil.field--type-text-long.field--label-above .field--item")
    if profil_block:
        paragraphs = profil_block.find_all("p")
        for p in paragraphs:
            p_text = p.get_text(strip=True).lower()
            if "expérienc" in p_text:
                match = re.search(r"(\d+)\s*(an|ans|année|années)", p_text)
                if match:
                    return match.group(0)
    return ""

def scrape_single_job(job):
    print(f"Scraping: {job['title']} ...")
    job_html = load_page(job["url"])
    if not job_html:
        return None
    try:
        soup = BeautifulSoup(job_html, "html.parser")

        contractype = get_field(soup, "field--name-field-type-de-contrat")
        location = get_field(soup, "field--name-field-emplacement")
        description = get_field(soup, "field.field--name-field-nos-valeurs-engagements.field--type-text-long.field--label-above")
        experience = extract_experience(soup)

        return {
            "title": job["title"],
            "contractype": contractype,
            "description": description,
            "company": "Wevioo",
            "location": location,
            "salary": "",
            "source": "Wevioo",
            "experience": experience,
            "published": "",
            "expires": "",
            "url": job["url"]
        }
    except Exception as e:
        print(f"Error while scraping {job['url']} : {e}")
        return None

def scrape_offers_list(html):
    soup = BeautifulSoup(html, "html.parser")
    offers = []
    job_cards = soup.select(".offer_emploi.container .col-md-12")
    for job in job_cards:
        title_tag = job.select_one(".title-offre-d-emploi")
        link_tag = job.select_one(".read-more-offre-d-emploi a")
        if title_tag and link_tag:
            title = title_tag.text.strip()
            relative_url = link_tag["href"]
            full_url = base_url + relative_url
            offers.append({"title": title, "url": full_url})
    return offers

def scrape_wevioo_jobs():
    html = load_page(listing_url)
    if not html:
        print("fail loading.")
        return []

    offers = scrape_offers_list(html)
    full_data = []

    with ThreadPoolExecutor(max_workers=10) as executor:
        futures = [executor.submit(scrape_single_job, job) for job in offers]
        for future in as_completed(futures):
            result = future.result()
            if result:
                full_data.append(result)
    return full_data
