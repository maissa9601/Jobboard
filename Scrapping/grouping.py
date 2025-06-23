import json
import os

def load_json(filepath):
    if not os.path.exists(filepath):
        print(f"file not found : {filepath}")
        return []
    with open(filepath, "r", encoding="utf-8") as f:
        return json.load(f)


wevioo_path = "Scrapping/wevioo/wevioo.json"
optioncarriere_path = "Scrapping/OptionCarriére/optioncarriere.json"
tanitjobs_path = "Scrapping/TanitCloudflare/tanitjobs.json"


wevioo_data = load_json(wevioo_path)
optioncarriere_data = load_json(optioncarriere_path)
tanitjobs_data = load_json(tanitjobs_path)


offers = wevioo_data + optioncarriere_data + tanitjobs_data


output_path = "Scrapping/offers.json"
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(offers, f, ensure_ascii=False, indent=4)

print(f"data saved {len(offers)} ")
