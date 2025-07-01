import json
from ScrapWevioo import scrape_wevioo_jobs

def main():
    data = scrape_wevioo_jobs()

    with open("wevioo.json", "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=4)

    print(f"\n{len(data)} offres enregistrées.")

if __name__ == "__main__":
    main()
