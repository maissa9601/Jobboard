import time
import logging
import os
import sys
from CloudflareBypasser import CloudflareBypasser
from DrissionPage import ChromiumPage, ChromiumOptions
from ScrapTanit import scrape_all_pages
import json

# Configuration du logger
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler('cloudflare_bypass.log', mode='w', encoding='utf-8')
    ]
)

# Création des options de Chromium
def get_chromium_options(browser_path: str, arguments: list) -> ChromiumOptions:
    options = ChromiumOptions().auto_port()
    options.set_paths(browser_path=browser_path)
    for argument in arguments:
        options.set_argument(argument)
    return options

# Fonction principale
def main():
    isHeadless = os.getenv('HEADLESS', 'false').lower() == 'true'
    browser_path = os.getenv('CHROME_PATH', r"C:/Program Files/Google/Chrome/Application/chrome.exe")

    if not os.path.exists(browser_path):
        logging.error(f"Chrome path not found: {browser_path}")
        sys.exit(1)

    logging.info(f"Mode Headless : {'activé' if isHeadless else 'désactivé'}")
    logging.info(f"Chrome utilisé : {browser_path}")

    arguments = [
        "--no-first-run",
        "--force-color-profile=srgb",
        "--metrics-recording-only",
        "--password-store=basic",
        "--use-mock-keychain",
        "--export-tagged-pdf",
        "--no-default-browser-check",
        "--disable-background-mode",
        "--enable-features=NetworkService,NetworkServiceInProcess,LoadCryptoTokenExtension,PermuteTLSExtensions",
        "--disable-features=FlashDeprecationWarning,EnablePasswordsAccountStorage",
        "--deny-permission-prompts",
        "--disable-gpu",
        "--accept-lang=en-US",
    ]

    if isHeadless:
        arguments.append('--headless=new')

    options = get_chromium_options(browser_path, arguments)
    driver = ChromiumPage(addr_or_opts=options)

    try:
        logging.info('Navigating to TanitJobs...')
        driver.get('https://www.tanitjobs.com/')

        logging.info('Bypassing Cloudflare protection...')
        cf_bypasser = CloudflareBypasser(driver)
        cf_bypasser.bypass()

        logging.info("Contenu accessible !")
        logging.info("Titre de la page : %s", driver.title)

        time.sleep(3)

        jobs = scrape_all_pages(driver, pages=5)
        logging.info(f"total offer scraped {len(jobs)}")

        # Sauvegarde
        with open("tanitjobs.json", "w", encoding="utf-8") as f:
            json.dump(jobs, f, ensure_ascii=False, indent=4)
        logging.info("data saved")

    except Exception as e:
        logging.error( str(e))
    finally:
        driver.quit()
        logging.info("Session closed")


# Point d’entrée
if __name__ == '__main__':
    main()
