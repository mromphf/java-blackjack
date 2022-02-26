#!/usr/bin/python

import sys
import json
from datetime import datetime


def write_file(file_name, csv_struct):
    csv_file_name = file_name.split(".")[0] + ".csv"
    with open(csv_file_name, "a") as file:
        for key in list(csv_struct.keys()):
            file.write(csv_struct[key])
        print("Wrote file: " + file.name)


def json_date_to_time(json_date):
    year = json_date["date"]["year"]
    month = json_date["date"]["month"]
    day = json_date["date"]["day"]
    hour = json_date["time"]["hour"]
    minute = json_date["time"]["minute"]
    second = json_date["time"]["second"]
    return datetime(year, month, day, hour, minute, second)


def json_to_csv(file_name):
    with open(file_name) as file:
        json_obj = json.load(file)
        key = json_obj["key"]
        name = json_obj["name"]
        timestamp = json_date_to_time(json_obj["created"])
        return {
            "header": "key,name,created\n",
            "row": (key + "," + name + "," + timestamp.isoformat() + "\n")
        }


def parse_file_path(file_path):
    exploded = file_path.split("/")
    return exploded[(len(exploded) - 1)]


def main(file_path):
    file_name = parse_file_path(file_path)
    csv_struct = json_to_csv(file_path)
    write_file(file_name, csv_struct)


if __name__ == "__main__":
    if len(sys.argv) < 1:
        print("Expected one argument. Quitting...")
        exit(1)

    main(sys.argv[1])
