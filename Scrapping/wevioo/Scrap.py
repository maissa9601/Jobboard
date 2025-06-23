from DrissionPage import ChromiumPage
from bs4 import BeautifulSoup
import requests
import time
import json
import re

base_url = "https://www.wevioo.com"
listing_url = f"{base_url}/fr/offres-d-emploi"
headers = {"User-Agent": "Mozilla/5.0"}

# Charger la page avec DrissionPage 
def load_page_with_drission(url):
    page = ChromiumPage()
    page.get(url)
    try:
        html = page.html  # récupère directement le HTML
        print(" Page chargée avec DrissionPage")
        return html
    except Exception as e:
        print(" Erreur DrissionPage :", e)
        return None

# Extraire un champ donné à partir de sa classe CSS dans une page détaillée
def get_field(soup, field_class):
    field = soup.select_one(f".{field_class} .field--item")
    return field.get_text(strip=True) if field else ""

def extract_experience_from_profil(soup):
    profil_block = soup.select_one(".field.field--name-field-profil.field--type-text-long.field--label-above .field--item")
    if profil_block:
        paragraphs = profil_block.find_all("p")
        for p in paragraphs:
            p_text = p.get_text(strip=True).lower()
            if "expérienc" in p_text:
                match = re.search(r"(\d+)\s*(an|ans|année|années)", p_text)
                if match:
                    return match.group(0)  # retourne seulement le chiffre
    return ""

def scrape_job_details(url, title):
    try:
        res = requests.get(url, headers=headers, verify=False)
        res.raise_for_status()
        soup = BeautifulSoup(res.text, "html.parser")

        contractype = get_field(soup, "field--name-field-type-de-contrat")
        location = get_field(soup, "field--name-field-emplacement")

        #  Description
        description_html = soup.select_one(".field.field--name-field-nos-valeurs-engagements.field--type-text-long.field--label-above")
        description = description_html.get_text(separator=" ", strip=True) if description_html else ""

        #  Expérience (nouvelle version)
        experience = extract_experience_from_profil(soup)

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
        print(f"Erreur lors du scraping de {url}: {e}")
        return None



# Scraper la liste des offres depuis la page principale
def scrape_offers_list(html=None):
    # Si on a pas fourni le html (DrissionPage), on le récupère avec requests
    if not html:
        response = requests.get(listing_url, headers=headers)
        response.raise_for_status()
        html = response.text

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

# ---- Programme principal ----
if __name__ == "__main__":
    # Tu peux tester avec DrissionPage (optionnel)
    html = load_page_with_drission(listing_url)

    # Récupération liste des offres (via DrissionPage ou requests)
    offers = scrape_offers_list(html)

    full_data = []

    for job in offers:
        print(f"Scraping: {job['title']} ...")
        details = scrape_job_details(job["url"], job["title"])
        if details:
            full_data.append(details)
        time.sleep(1)  # pause pour ne pas surcharger le serveur

    # Sauvegarde en JSON
    with open("wevioo.json", "w", encoding="utf-8") as f:
        json.dump(full_data, f, ensure_ascii=False, indent=4)

    print("Scraping terminé. Données enregistrées dans 'wevioo.json'")
