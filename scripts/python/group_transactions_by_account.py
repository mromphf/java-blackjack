#!usr/local/bin/python3.9

import os
import pandas as pd
from csv import DictReader

SOURCE_DIR = "../app-data/accounts/"
GROUPED_DIR = "../app-data/accounts-grouped/"
KEY = "key"


def compile_csv_files(csv_files):
    dictionaries = []
    for file in csv_files:
        with open(file, 'r') as csv_contents:
            for csv_row in DictReader(csv_contents):
                dictionaries.append({key: csv_row[key] for key in csv_row.keys()})
    return dictionaries


def qualified_file_name(filename):
    return SOURCE_DIR + filename


if __name__ == "__main__":
    files = os.listdir(SOURCE_DIR)
    dicts = compile_csv_files(map(qualified_file_name, files))
    df = pd.DataFrame(dicts)
    for account_key in list(df[KEY].unique()):
        filename = f"{GROUPED_DIR}{account_key}.csv"
        df[df[KEY] == account_key].to_csv(filename, index=False)
