
import requests
from bs4 import BeautifulSoup, NavigableString, Tag
import re


def extract_experience(soup):
    b_tag = soup.find('b', string=lambda text: text and "Expérience souhaitée" in text)
    if not b_tag:
        return None

    found = False
    for sibling in b_tag.next_siblings:
        if isinstance(sibling, Tag):
            # Ignorer les <span class="br"> uniquement une fois
            if sibling.name == 'span' and 'br' in sibling.get('class', []):
                found = True
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
    
    # Cherche une ligne qui contient "Date de publication :"
    match = re.search(r'Date de publication\s*:\s*(\d{2}/\d{2}/\d{4})', text)
    if match:
        return match.group(1)  # ex: '19/06/2025'
    return None

def extract_salary(soup):
    details_ul = soup.find('ul', class_='details')
    if not details_ul:
        return None
    
    for li in details_ul.find_all('li'):
        svg_use = li.find('use')
        if svg_use and svg_use.get('xlink:href') == '#icon-money':
            # récupérer le texte dans li, sans l'icône svg
            # on extrait tout le texte et on enlève le texte des balises svg
            for svg in li.find_all('svg'):
                svg.decompose()  # enlève la balise svg pour ne garder que le texte
            salary_text = li.get_text(strip=True)
            return salary_text
    return None

def extract_job_description(soup):
    section = soup.find('section', class_='content')
    if not section:
        return None

    # Chercher la balise <b> contenant "Description du poste"
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

    # Cas 2 : Bloc "Poste proposé" jusqu'au <span class="br"></span>
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


def extract_contract_type(soup):
    ul_details = soup.find('ul', class_='details')
    if not ul_details:
        return None

    for li in ul_details.find_all('li'):
        use_tag = li.find('use')
        if use_tag and use_tag.get('xlink:href') == '#icon-contract2':
            return li.get_text(strip=True)
    return None


def scrape_offer_details(offer_url):
    response = requests.get(offer_url)
    if response.status_code != 200:
        print(f"Erreur lors de l'accès à l'offre : {offer_url}")
        return {}

    soup = BeautifulSoup(response.text, 'html.parser')

    # Type de contrat précis
    contract = extract_contract_type(soup)

    # Description précise du poste
    description = extract_job_description(soup)

    # Expérience
    experience = extract_experience(soup)
    #Salary
    salary=extract_salary(soup)
    #date de publication
    published=extract_date_publication(soup)
    

    return {
        "contractype": contract,
        "description": description,
        "experience": experience,
        "salary": salary,
        "expires": None,
        "published":published
    }


def scrape_optioncarriere_jobs(url):
    response = requests.get(url)
    if response.status_code != 200:
        print(f"Erreur HTTP : {response.status_code}")
        return []

    soup = BeautifulSoup(response.text, 'html.parser')
    jobs = []

    articles = soup.find_all('article', class_='job clicky')

    for article in articles:
        # Titre de l'offre
        title_tag = article.find('h2').find('a')
        title = title_tag.get_text(strip=True)
        relative_url = title_tag['href']
        full_url = f"https://www.optioncarriere.tn{relative_url}"

        # Société
        company_tag = article.find('p', class_='company')
        if company_tag:
            a = company_tag.find('a')
            company = a.get_text(strip=True) if a else company_tag.get_text(strip=True)
        else:
            company = None


        location_tag = article.find('ul', class_='location')
        location = location_tag.find('li').get_text(strip=True) if location_tag else None

       
   

        # Scraper la page de l'offre pour avoir plus de détails
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

    return jobs

# Exemple
url = "https://www.optioncarriere.tn/emploi?s=&l=tunisie"
jobs = scrape_optioncarriere_jobs(url)
print(f"{len(jobs)} offres extraites")
for job in jobs[:3]:
    print(job)
