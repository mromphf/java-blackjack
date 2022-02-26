import os
import pandas as pd


def main():
    pd.concat([pd.DataFrame(pd.read_csv(f'../../app-data/transactions-grouped/{csv_file}'))
               for csv_file
               in os.listdir('../../app-data/transactions-grouped')]) \
        .set_index("time") \
        .to_csv('../../app-data/aggregates/aggregate.csv')
    print(f"Finished printing csv.")


if __name__ == '__main__':
    main()
