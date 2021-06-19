package main.io.storage;

import com.google.gson.Gson;
import main.Config;
import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static main.io.storage.FileFunctions.*;
import static main.io.util.JsonUtil.*;

public class FileSystem implements Memory {

    private final File accountsDir;
    private final File transactionsDir;
    private final File decksDir;

    public FileSystem(String accountsPath, String transactionsPath, String decksPath) {
        this.accountsDir = new File(accountsPath);
        this.transactionsDir = new File(transactionsPath);
        this.decksDir = new File(decksPath);

        if (accountsDir.mkdir()) {
            System.out.printf("Created new directory: %s\n", accountsDir.getPath());
        }

        if (transactionsDir.mkdir()) {
            System.out.printf("Created new directory: %s\n", transactionsDir.getPath());
        }

        if (decksDir.mkdir()) {
            System.out.printf("Created new directory: %s\n", decksDir.getPath());
        }
    }

    public Config loadConfig() {
        try {
            final File configFile = new File(FileSystem.class.getResource("/config/config.json").getPath());
            return new Gson().fromJson(fileToJson(configFile), Config.class);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public Stack<Card> loadDeck(String name) {
        try {
            final File deckFile = new File(String.format("%s/%s.json", decksDir, name));
            return deckFromJson(fileToJson(deckFile));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public Set<Account> loadAllAccounts() {
        final Set<Account> accounts = new HashSet<>();
        final List<Transaction> transactions = loadAllTransactions();

        try {
            for (File file : allFilesInDir(accountsDir)) {
                accounts.add(loadAccount(file).updateBalance(transactions));
            }
        } catch (IOException e) {
            System.out.println("Could not load accounts file!");
            return accounts;
        } catch (NoSuchElementException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }

        return accounts;
    }

    @Override
    public List<Transaction> loadAllTransactions() {
        List<Transaction> transactions = new LinkedList<>();

        try {
            for (File file : allFilesInDir(transactionsDir)) {
                for (String line : readCsvLines(file)) {
                    String[] row = line.split(",");
                    transactions.add(new Transaction(
                            LocalDateTime.parse(row[0]),
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
        final String transactionFilename = transactionsDir.getPath() + "/" + dateBasedFileName(transaction.getTime());
        final File transactionsFile = new File(transactionFilename);

        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(transactionsFile, true));

            if (transactionsFile.length() == 0) {
                writer.println("time,accountKey,description,amount");
            }

            writer.println(transaction);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveNewAccount(Account account) {
        final File accountFile = accountFileFromKey(account.getKey());

        try {
            final FileWriter fileWriter = new FileWriter(accountFile);
            fileWriter.write(new Gson().toJson(account));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void deleteAccount(Account account) {
        final File accountFile = accountFileFromKey(account.getKey());
        accountFile.delete();
    }

    private Account loadAccount(File file) throws IOException {
        return new Gson().fromJson(fileToJson(file), Account.class);
    }

    private File accountFileFromKey(UUID accountKey) {
        return new File(accountsDir.getPath() + "/" + accountKey + ".json");
    }
}
