package main.common;

import main.domain.Account;
import main.domain.Transaction;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class CsvUtil {

    private static final String ACCOUNT_HEADER = "key,name,created";
    private static final String TRANSACTION_HEADER = "time,accountKey,description,amount";

    public static String toCsvRow(Account account) {
        final String zonedTimestamp = account.getCreated().atZone(systemDefault()).format(ISO_OFFSET_DATE_TIME);

        return String.format("%s,%s,%s", account.getKey(), account.getName(), zonedTimestamp);
    }

    public static String toCsvHeader(Account account) {
        return ACCOUNT_HEADER;
    }

    public static String toCsvRow(Transaction transaction) {
        final String zonedTimestamp = transaction.getTime().atZone(systemDefault()).format(ISO_OFFSET_DATE_TIME);

        return String.format("%s,%s,%s,%s",
                zonedTimestamp,
                transaction.getAccountKey(),
                transaction.getDescription(),
                transaction.getAmount());
    }

    public static String toCsvHeader(Transaction transaction) {
        return TRANSACTION_HEADER;
    }
}
