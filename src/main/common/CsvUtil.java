package main.common;

import main.domain.Account;
import main.domain.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class CsvUtil {

    public static final String ACCOUNT_HEADER = "key,name,created";
    public static final String ACCOUNT_CLOSURE_HEADER = "timestamp, accountKey";
    public static final String TRANSACTION_HEADER = "time,accountKey,description,amount";

    public static String toCsvRow(Account account) {
        final String zonedTimestamp = account.getCreated().atZone(systemDefault()).format(ISO_OFFSET_DATE_TIME);

        return String.format("%s,%s,%s", account.getKey(), account.getName(), zonedTimestamp);
    }

    public static String toCsvRow(Transaction transaction) {
        final String zonedTimestamp = transaction.getTime().atZone(systemDefault()).format(ISO_OFFSET_DATE_TIME);

        return String.format("%s,%s,%s,%s",
                zonedTimestamp,
                transaction.getAccountKey(),
                transaction.getDescription(),
                transaction.getAmount());
    }

    public static String accountClosureRow(Account account) {
        final String zonedTimestamp = now().atZone(systemDefault()).format(ISO_OFFSET_DATE_TIME);
        return String.format("%s,%s", zonedTimestamp, account.getKey());
    }

    public static Account accountFromCsvRow(String[] delineatedRow) {
        return new Account(
                UUID.fromString(delineatedRow[0]),
                delineatedRow[1],
                ZonedDateTime.parse(delineatedRow[2]).toLocalDateTime()
        );
    }

    public static Transaction transactionsFromCsvRow(String[] delineatedRow) {
        return new Transaction(
                ZonedDateTime.parse(delineatedRow[0]).toLocalDateTime(),
                UUID.fromString(delineatedRow[1]),
                delineatedRow[2],
                Integer.parseInt(delineatedRow[3])
        );
    }

    public static SortedMap<LocalDateTime, UUID> accountClosuresFromCsvRow(String[] delineatedRow) {
        final SortedMap<LocalDateTime, UUID> timestampedClosures = new TreeMap<>();

        timestampedClosures.put(
                ZonedDateTime.parse(delineatedRow[0]).toLocalDateTime(),
                UUID.fromString(delineatedRow[1])
        );

        return timestampedClosures;
    }

    public static void appendToCsv(File file, String header, String row) {
        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(file, true));

            if (file.length() == 0) {
                writer.println(header);
            }

            writer.println(row);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void appendToFile(File file, String row) {
        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(file, true));

            writer.println(row);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
