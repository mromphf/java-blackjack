package main.io.storage;

import com.google.gson.Gson;
import main.Config;
import main.common.CsvUtil;
import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    public Config loadConfig() {
        try {
            final File configFile = new File(FileSystem.class.getResource("/config/config.json").getPath());
            return new Gson().fromJson(fileToJson(configFile), Config.class);
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

    @Override
    public void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final String transactionFilename = directories.get(TRANSACTIONS).getPath() + "/" + dateBasedCsvFileName(transaction.getTime());
        appendToCsv(new File(transactionFilename), TRANSACTION_HEADER, toCsvRow(transaction));
    }

    @Override
    public void saveNewAccount(Account account) {
        final String accountFile = directories.get(ACCOUNTS).getPath() + "/" + dateBasedCsvFileName(account.getCreated());
        appendToCsv(new File(accountFile), ACCOUNT_HEADER, toCsvRow(account));
    }

    @Override
    public void deleteAccount(Account account) {
        final File accountFile = new File(directories.get(ACCOUNTS_CLOSED).getPath() + "/" +
                        dateBasedCsvFileName(account.getCreated()));

        appendToCsv(accountFile, ACCOUNT_CLOSURE_HEADER, accountClosureRow(account));
    }

    private void appendToCsv(File file, String header, String row) {
        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(file, true));

            if (file.length() == 0) {
                writer.println(header);
            }

            writer.println(row);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
