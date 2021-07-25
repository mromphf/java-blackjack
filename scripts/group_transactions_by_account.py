#!usr/bin/python

import os
from csv import DictReader
from dateutil import parser
from uuid import UUID


def compile_csv_files(csv_files):
    dictionaries = []
    format = "%Y-%m-%d %H:%M:%S"
    for file in csv_files:
        with open(file, 'r') as csv_contents:
            reader = DictReader(csv_contents)
            for reader_line in reader:
                dictionaries.append({
                    "time": parser.parse(reader_line["time"]),
                    "accountKey": UUID(reader_line["accountKey"]),
                    "description": reader_line["description"],
                    "amount": reader_line["amount"]
                })
    return dictionaries


def qualified_file_name(filename):
    return "../app-data/transactions/" + filename


if __name__ == "__main__":
    csv_files = os.listdir("../app-data/transactions")
    dicts = compile_csv_files(map(qualified_file_name, csv_files))
    for d in dicts:
        print d
