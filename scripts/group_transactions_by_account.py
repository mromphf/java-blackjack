#!usr/local/bin/python3.9

import os
import pandas as pd
from csv import DictReader

TRANSACTIONS_DIR = "../app-data/transactions/"


def compile_csv_files(csv_files):
    dictionaries = []
    for file in csv_files:
        with open(file, 'r') as csv_contents:
            for csv_row in DictReader(csv_contents):
                dictionaries.append({key: csv_row[key] for key in csv_row.keys()})
    return dictionaries


def qualified_file_name(filename):
    return TRANSACTIONS_DIR + filename


if __name__ == "__main__":
    files = os.listdir(TRANSACTIONS_DIR)
    dicts = compile_csv_files(map(qualified_file_name, files))
    df = pd.DataFrame(dicts)
    for account_key in list(df["accountKey"].unique()):
        filename = f"../app-data/transactions-grouped/{account_key}.csv"
        df[df['accountKey'] == account_key].to_csv(filename, index=False)
