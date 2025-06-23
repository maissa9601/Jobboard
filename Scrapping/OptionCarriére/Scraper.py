import requests
from bs4 import BeautifulSoup, NavigableString, Tag
import re
from DrissionPage import ChromiumPage
import json
import time


# Chargement de la page avec DrissionPage
def load_page(url):
    page = ChromiumPage()
    try:
        page.get(url)
        html = page.html
        print(f"success : {url}")
        return html
    except Exception as e:
        print(f"error while scraping {url} : {e}")
        return None
    finally:
        page.close()


def extract_experience(soup):
    b_tag = soup.find('b', string=lambda text: text and "Expérience souhaitée" in text)
    if not b_tag:
        return None

    for sibling in b_tag.next_siblings:
        if isinstance(sibling, Tag):
            if sibling.name == 'span' and 'br' in sibling.get('class', []):
                continue
            text = sibling.get_text(strip=True)
            if text:
                return text
        elif isinstance(sibling, NavigableString):
            text = sibling.strip()
            if text:
                return text

    return None


def extract_date_publication(soup):
    content_section = soup.find('section', class_='content')
    if not content_section:
        return None

    text = content_section.get_text(separator='\n', strip=True)
    match = re.search(r'Date de publication\s*:\s*(\d{2}/\d{2}/\d{4})', text)
    if match:
        return match.group(1)
    return None


def extract_salary(soup):
    details_ul = soup.find('ul', class_='details')
    if not details_ul:
        return None

    for li in details_ul.find_all('li'):
        svg_use = li.find('use')
        if svg_use and svg_use.get('xlink:href') == '#icon-money':
            for svg in li.find_all('svg'):
                svg.decompose()
            salary_text = li.get_text(strip=True)
            return salary_text
    return None


def extract_job_description(soup):
    section = soup.find('section', class_='content')
    if not section:
        return None

    desc_poste_b = section.find('b', string=re.compile(r'Description du poste', re.IGNORECASE))
    if desc_poste_b:
        content = []
        for sibling in desc_poste_b.next_siblings:
            if isinstance(sibling, Tag) and sibling.name == 'b':
                break
            if isinstance(sibling, NavigableString):
                text = sibling.strip()
                if text:
                    content.append(text)
            elif isinstance(sibling, Tag):
                text = sibling.get_text(separator='\n', strip=True)
                if text:
                    content.append(text)
        result = '\n'.join(content).strip()
        if result:
            return result

    b_tag = section.find('b', string=lambda text: text and "Poste proposé" in text)
    if b_tag:
        found_br = False
        for sibling in b_tag.next_siblings:
            if isinstance(sibling, Tag):
                if sibling.name == "span" and 'br' in sibling.get('class', []):
                    found_br = True
                    continue
                elif found_br:
                    text = sibling.get_text(strip=True)
                    if text:
                        return text
            elif isinstance(sibling, NavigableString) and found_br:
                text = sibling.strip()
                if text:
                    return text
    return None


def extract_contract_type(soup):
    ul_details = soup.find('ul', class_='details')
    if not ul_details:
        return None

    for li in ul_details.find_all('li'):
        use_tag = li.find('use')
        if use_tag and use_tag.get('xlink:href') == '#icon-contract2':
            return li.get_text(strip=True)
    return None


# Scraper les détails d'une offre
def scrape_offer_details(offer_url):
    html = load_page(offer_url)
    if not html:
        print(f"error loading offer {offer_url}")
        return {
            "contractype": None,
            "description": None,
            "experience": None,
            "salary": None,
            "expires": None,
            "published": None
        }

    soup = BeautifulSoup(html, 'html.parser')
    return {
        "contractype": extract_contract_type(soup),
        "description": extract_job_description(soup),
        "experience": extract_experience(soup),
        "salary": extract_salary(soup),
        "expires": None,
        "published": extract_date_publication(soup)
    }


# Scraper la liste des offres
def scrape_jobs(url):
    html = load_page(url)
    if not html:
        print("error loading principale page ")
        return []

    soup = BeautifulSoup(html, 'html.parser')
    jobs = []
    articles = soup.find_all('article', class_='job clicky')

    for article in articles:
        try:
            title_tag = article.find('h2').find('a')
            title = title_tag.get_text(strip=True)
            relative_url = title_tag['href']
            full_url = f"https://www.optioncarriere.tn{relative_url}"

            print(f"\n Scraping: {title} - {full_url}")

            company_tag = article.find('p', class_='company')
            if company_tag:
                a = company_tag.find('a')
                company = a.get_text(strip=True) if a else company_tag.get_text(strip=True)
            else:
                company = None

            location_tag = article.find('ul', class_='location')
            location = location_tag.find('li').get_text(strip=True) if location_tag else None

            details = scrape_offer_details(full_url)

            jobs.append({
                "title": title,
                "contractype": details["contractype"],
                "description": details["description"],
                "company": company,
                "location": location,
                "salary": details["salary"],
                "source": "OptionCarriere",
                "experience": details["experience"],
                "published": details["published"],
                "expires": details["expires"],
                "url": full_url
            })

            time.sleep(1)  # Pause 

        except Exception as e:
            print(f"error while scraping offer{e}")
            continue

    return jobs
def scrape_all_pages(base_url, max_pages=5):
    all_jobs = []
    for i in range(max_pages):
        start = i * 10
        paginated_url = f"{base_url}&start={start}" if "?" in base_url else f"{base_url}?start={start}"
        print(f"\n Scraping page {i+1}: {paginated_url}")
        jobs = scrape_jobs(paginated_url)
        if not jobs:
            print("No offer.")
            break
        all_jobs.extend(jobs)
        time.sleep(1)  # pause
    return all_jobs
    



if __name__ == "__main__":
    base_url = "https://www.optioncarriere.tn/emploi?s=&l=tunisie"
    job_data = scrape_all_pages(base_url, max_pages=10)  

    with open("optioncarriere.json", "w", encoding="utf-8") as f:
        json.dump(job_data, f, ensure_ascii=False, indent=4)

    print(f"\n {len(job_data)} data saved")

