#!usr/bin/python

import os
from csv import DictReader
from dateutil import parser
from uuid import UUID


def compile_csv_files(csv_files):
    dictionaries = []
    for file in csv_files:
        with open(file, 'r') as csv_contents:
            for csv_row in DictReader(csv_contents):
                dictionaries.append({key:csv_row[key] for key in csv_row.keys()})
    return dictionaries


def qualified_file_name(filename):
    return "../app-data/transactions/" + filename


if __name__ == "__main__":
    csv_files = os.listdir("../app-data/transactions")
    dicts = compile_csv_files(map(qualified_file_name, csv_files))
    for d in dicts:
        print d
