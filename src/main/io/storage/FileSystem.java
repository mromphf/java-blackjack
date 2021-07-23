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
            final File deckFile = new File(String.format("%s/%s.json", directories.get(DECKS), name));
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
        final File file = inferFile(TRANSACTIONS, transaction.getTime());
        appendToCsv(file, TRANSACTION_HEADER, toCsvRow(transaction));
    }

    @Override
    public void openAccount(Account account) {
        final File file = inferFile(ACCOUNTS, account.getCreated());
        appendToCsv(file, ACCOUNT_HEADER, toCsvRow(account));
    }

    @Override
    public void closeAccount(Account account) {
        final File file = inferFile(ACCOUNTS_CLOSED, account.getCreated());
        appendToCsv(file, ACCOUNT_CLOSURE_HEADER, accountClosureRow(account));
    }

    public Map<LocalDateTime, UUID> loadAllClosedAccountKeys() {
        final File accountsClosedDir = directories.get(ACCOUNTS_CLOSED);

        return stream(allFilesInDir(accountsClosedDir))
                .map(FileFunctions::readCsvLines)
                .flatMap(Collection::stream)
                .map(line -> line.split(","))
                .map(CsvUtil::accountClosuresFromCsvRow)
                .collect(Collectors.toMap(SortedMap::firstKey, v -> v.get(v.firstKey())));
    }

    @Override
    public Set<Account> loadAllAccounts() {
        final List<Transaction> transactions = loadAllTransactions();
        final File accountsDir = directories.get(ACCOUNTS);
        final Set<UUID> closedAccountKeys = new HashSet<>(loadAllClosedAccountKeys().values());

        return stream(allFilesInDir(accountsDir))
                .map(FileFunctions::readCsvLines)
                .flatMap(Collection::stream)
                .map(line -> line.split(","))
                .map(CsvUtil::accountFromCsvRow)
                .filter(act -> !closedAccountKeys.contains(act.getKey()))
                .map(act -> act.updateBalance(transactions))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Transaction> loadAllTransactions() {
        final File transactionsDir = directories.get(TRANSACTIONS);

        return stream(allFilesInDir(transactionsDir))
                .map(FileFunctions::readCsvLines)
                .flatMap(Collection::stream)
                .map(line -> line.split(","))
                .map(CsvUtil::transactionsFromCsvRow)
                .collect(Collectors.toList());
    }

    private File inferFile(Directory directory, LocalDateTime timestamp) {
        return new File(directories.get(directory).getPath() + "/" + dateBasedCsvFileName(timestamp));
    }
}
