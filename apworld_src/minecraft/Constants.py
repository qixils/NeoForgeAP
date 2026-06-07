import json
import pkgutil


def load_data_file(*args) -> dict:
    fname = "/".join(["data", *args])
    return json.loads(pkgutil.get_data(__name__, fname).decode())


item_info = load_data_file("items.json")
item_name_to_id = {name: index \
                   for index, name in enumerate(item_info["all_items"], start=1)}

location_info = load_data_file("locations.json")
location_name_to_id = {name: index \
                       for index, name in enumerate(location_info["all_locations"], start=1)}

exclusion_info = load_data_file("excluded_locations.json")

region_info = load_data_file("regions.json")
