import logging
import json
import os
import sys
from DrissionPage import ChromiumPage, ChromiumOptions
from ScrapIndeed import scrape_all_cities

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('cloudflare_bypass.log', mode='w', encoding='utf-8')
    ]
)

def get_chromium_options(browser_path: str, arguments: list) -> ChromiumOptions:
    options = ChromiumOptions().auto_port()
    options.set_paths(browser_path=browser_path)
    for argument in arguments:
        options.set_argument(argument)
    return options

def main():
    isHeadless = os.getenv('HEADLESS', 'false').lower() == 'true'
    browser_path = os.getenv('CHROME_PATH', r"C:/Program Files/Google/Chrome/Application/chrome.exe")

    LIEUX = [
        "Paris", "Lyon", "Marseille", "Toulouse", "Nice", "Nantes", "Strasbourg",
        "Montpellier", "Bordeaux", "Lille", "Rennes", "Reims", "Le Havre",
        "Saint-Étienne", "Toulon"
    ]

    if not os.path.exists(browser_path):
        logging.error(f"Chrome path not found: {browser_path}")
        sys.exit(1)

    logging.info(f"Mode Headless : {'activé' if isHeadless else 'désactivé'}")
    logging.info(f"Chrome utilisé : {browser_path}")

    arguments = [
        "--no-first-run", "--force-color-profile=srgb", "--metrics-recording-only",
        "--password-store=basic", "--use-mock-keychain", "--export-tagged-pdf",
        "--no-default-browser-check", "--disable-background-mode",
        "--enable-features=NetworkService,NetworkServiceInProcess,LoadCryptoTokenExtension,PermuteTLSExtensions",
        "--disable-features=FlashDeprecationWarning,EnablePasswordsAccountStorage",
        "--deny-permission-prompts", "--disable-gpu", "--accept-lang=en-US",
    ]
    if isHeadless:
        arguments.append('--headless=new')

    options = get_chromium_options(browser_path, arguments)
    driver = ChromiumPage(addr_or_opts=options)

    try:
        logging.info('loading Indeed...')
        driver.get("https://fr.indeed.com")

        all_jobs = scrape_all_cities(driver, LIEUX, max_pages=1)

        filename = "indeed.json"
        with open(filename, "w", encoding="utf-8") as f:
            json.dump(all_jobs, f, ensure_ascii=False, indent=4)

        logging.info(f"Data saved {filename}")

    except Exception as e:
        logging.error({e})

    finally:
        driver.quit()


if __name__ == '__main__':
    main()
