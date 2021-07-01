package main.io.storage;

import com.google.gson.Gson;
import main.Config;
import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.exit;
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

    @Override
    public Set<Account> loadAllAccounts() {
        final Set<Account> accounts = new HashSet<>();
        final List<Transaction> transactions = loadAllTransactions();

        try {
            for (File file : allFilesInDir(directories.get(ACCOUNTS))) {
                for (String line : readCsvLines(file)) {
                    String[] row = line.split(",");
                    accounts.add(new Account(
                            UUID.fromString(row[0]),
                            row[1],
                            ZonedDateTime.parse(row[2]).toLocalDateTime()
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load accounts file!");
            return accounts;
        } catch (NoSuchElementException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            exit(1);
        }

        return accounts.stream()
                .map(act -> act.updateBalance(transactions))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Transaction> loadAllTransactions() {
        List<Transaction> transactions = new LinkedList<>();

        try {
            for (File file : allFilesInDir(directories.get(TRANSACTIONS))) {
                for (String line : readCsvLines(file)) {
                    String[] row = line.split(",");
                    transactions.add(new Transaction(
                            ZonedDateTime.parse(row[0]).toLocalDateTime(),
                            UUID.fromString(row[1]),
                            row[2],
                            Integer.parseInt(row[3])
                    ));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final String transactionFilename = directories.get(TRANSACTIONS).getPath() + "/" + dateBasedCsvFileName(transaction.getTime());
        appendToCsv(transactionFilename, transaction);
    }

    @Override
    public void saveNewAccount(Account account) {
        final String accountFile = directories.get(ACCOUNTS).getPath() + "/" + dateBasedCsvFileName(account.getCreated());
        appendToCsv(accountFile, account);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void deleteAccount(Account account) {
        final File accountFile = accountFileFromKey(account.getKey());
        accountFile.delete();
    }

    private File accountFileFromKey(UUID accountKey) {
        return new File(directories.get(ACCOUNTS).getPath() + "/" + accountKey + ".csv");
    }

    private void appendToCsv(String filename, Account account) {
        final File file = new File(filename);
        appendToCsv(file, ACCOUNT_HEADER, toCsvRow(account));
    }

    private void appendToCsv(String filename, Transaction transaction) {
        final File transactionsFile = new File(filename);
        appendToCsv(transactionsFile, TRANSACTION_HEADER, toCsvRow(transaction));
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
