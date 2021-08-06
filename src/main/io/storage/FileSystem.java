package main.io.storage;

import main.common.CsvUtil;
import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.exit;
import static java.util.Arrays.stream;
import static main.common.CsvUtil.*;
import static main.common.JsonUtil.deckFromJson;
import static main.io.storage.Directory.*;
import static main.io.storage.FileFunctions.*;

public class FileSystem implements Memory {

    private final Map<Directory, File> directories;

    public FileSystem(Map<Directory, File> directories) {
        this.directories = directories;

        for (File file : directories.values()) {
            if (file.mkdir()) {
                System.out.printf("Created new directory: %s\n", file.getPath());
            }
        }
    }

    public Properties loadConfig() {
        try {
            final Properties properties = new Properties();
            properties.load(FileSystem.class.getResourceAsStream("/config/dev.conf"));
            return properties;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            exit(1);
            return null;
        }
    }

    @Override
    public Stack<Card> loadDeck(String name) {
        try {
            final File deckFile = new File(format("%s/%s.json", directories.get(DECKS), name));
            return deckFromJson(fileToJson(deckFile));
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
            return null;
        }
    }

    @Override
    public void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final File file = inferTransactionsFile(transaction.getAccountKey());
        appendToCsv(file, TRANSACTION_HEADER, toCsvRow(transaction));
    }

    @Override
    public void openAccount(Account account) {
        final File file = inferFile(ACCOUNTS, account.getCreated());
        appendToCsv(file, ACCOUNT_HEADER, toCsvRow(account));
    }

    @Override
    public void closeAccount(Account account) {
        final File file = directories.get(ACCOUNTS_CLOSED);
        appendToCsv(file, ACCOUNT_CLOSURE_HEADER, accountClosureRow(account));
    }

    @Override
    public Map<LocalDateTime, UUID> loadAllClosedAccountKeys() {
        final File accountsClosedDir = directories.get(ACCOUNTS_CLOSED);

        return readCsvLines(accountsClosedDir).stream()
                .map(line -> line.split(","))
                .map(CsvUtil::accountClosuresFromCsvRow)
                .collect(Collectors.toMap(SortedMap::firstKey, v -> v.get(v.firstKey())));
    }

    @Override
    public Set<Account> loadAllAccounts(Collection<UUID> closedAccountKeys) {
        final File accountsDir = directories.get(ACCOUNTS);

        return readCsvLines(accountsDir).stream()
                .map(line -> line.split(","))
                .map(CsvUtil::accountFromCsvRow)
                .filter(act -> !closedAccountKeys.contains(act.getKey()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Transaction> loadAllTransactions(Collection<UUID> closedAccountKeys) {
        final File transactionsDir = directories.get(TRANSACTIONS);

        return stream(allFilesInDir(transactionsDir))
                .filter(f -> !closedAccountKeys.contains(uuidFromCsvFileName(f)))
                .map(FileFunctions::readCsvLines)
                .flatMap(Collection::stream)
                .map(line -> line.split(","))
                .map(CsvUtil::transactionsFromCsvRow)
                .collect(Collectors.toList());
    }

    private UUID uuidFromCsvFileName(File f) {
        return UUID.fromString(f.getName().split("\\.")[0]);
    }

    private File inferTransactionsFile(UUID key) {
        return new File(format("%s/%s.csv", directories.get(TRANSACTIONS).getPath(), key));
    }
}
