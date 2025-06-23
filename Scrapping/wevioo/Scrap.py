from DrissionPage import ChromiumPage
from bs4 import BeautifulSoup
import time
import json
import re

base_url = "https://www.wevioo.com"
listing_url = f"{base_url}/fr/offres-d-emploi"


# Chargement de la page avec 
def load_page(url):
    page = ChromiumPage()
    try:
        page.get(url)
        html = page.html
        print(f"success: {url}")
        return html
    except Exception as e:
        print(f"error while scraping{url} : {e}")
        return None
    finally:
        page.close()  

# extraire un champ texte à partir d'une classe CSS
def get_field(soup, field_class):
    field = soup.select_one(f".{field_class} .field--item")
    return field.get_text(strip=True) if field else ""

# extraire l'expérience 
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

# scrapper depuis la page détail
def scrape_details(html, title, url):
    try:
        soup = BeautifulSoup(html, "html.parser")

        contractype = get_field(soup, "field--name-field-type-de-contrat")
        
        location = get_field(soup, "field--name-field-emplacement")

        
        description = get_field(soup, "field.field--name-field-nos-valeurs-engagements.field--type-text-long.field--label-above")

        experience = extract_experience(soup)

        return {
            "title": title,
            "contractype": contractype,
            "description": description,
            "company": "Wevioo",
            "location": location,
            "salary": "",
            "source": "Wevioo",
            "experience": experience,
            "published": "",
            "expires": "",
            "url": url
        }

    except Exception as e:
        print(f"Erreur lors du scraping de {url} : {e}")
        return None

# Scraper la liste des offres depuis la page principale
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

            offers.append({
                "title": title,
                "url": full_url
            })

    return offers


if __name__ == "__main__":
    html = load_page(listing_url)
    if not html:
        print("Échec du chargement de la page principale, arrêt du script.")
        exit(1)

    offers = scrape_offers_list(html)

    full_data = []

    for job in offers:
        print(f"Scraping: {job['title']} ...")
        job_html = load_page(job["url"])
        if not job_html:
            print(f"Échec du chargement de la page de l'offre: {job['url']}")
            continue
        details = scrape_details(job_html, job["title"], job["url"])
        if details:
            full_data.append(details)
        time.sleep(1)  # pause

    # Sauvegarde 
    with open("wevioo.json", "w", encoding="utf-8") as f:
        json.dump(full_data, f, ensure_ascii=False, indent=4)

    print("data saved")
