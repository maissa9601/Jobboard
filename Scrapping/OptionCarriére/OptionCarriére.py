
import json
import time
from ScrapeOption import scrape_all_pages

def main():
    base_url = "https://www.optioncarriere.tn/emploi?s=&l=tunisie"
    start_time = time.time()
    job_data = scrape_all_pages(base_url, max_pages=10)

    with open("optioncarriere.json", "w", encoding="utf-8") as f:
        json.dump(job_data, f, ensure_ascii=False, indent=4)

    print(f"\n{len(job_data)}offers saved ")
    print(f"total time: {time.time() - start_time:.2f} secondes")

if __name__ == "__main__":
    main()
