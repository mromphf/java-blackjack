package main.io.storage;

import com.oracle.javafx.jmx.json.JSONDocument;
import com.oracle.javafx.jmx.json.impl.JSONStreamReaderImpl;
import main.domain.Account;
import main.domain.Transaction;
import main.io.util.JsonUtil;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class MemoryFiles implements Memory {

    public final File accountsDir;
    public final File transactionsDir;

    public MemoryFiles() {
        accountsDir = new File("./accounts/");
        transactionsDir = new File("./transactions/");

        if (accountsDir.mkdir()) {
            System.out.printf("Created new directory: %s\n", accountsDir.getPath());
        }

        if (transactionsDir.mkdir()) {
            System.out.printf("Created new directory: %s\n", transactionsDir.getPath());
        }
    }

    public Map<String, Object> loadConfig() {
        try {
            final File configFile = new File(MemoryFiles.class.getResource("/config/config.json").getPath());
            return JsonUtil.configFromJson(fileToJsonDocument(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public Set<Account> loadAllAccounts() {
        Set<Account> accounts = new HashSet<>();
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
                final Scanner sc = new Scanner(file);

                // Assume header row
                sc.nextLine();

                while (sc.hasNextLine()) {
                    String[] row = sc.nextLine().split(",");
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
    public void saveTransactions(List<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final String transactionFilename = transactionsDir.getPath() + "/" + fileName(transaction.getTime());
        final File transactionsFile = new File(transactionFilename);

        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(transactionsFile, true));

            if (transactionsFile.length() == 0) {
                writer.println("time,accountKey,description,amount");
            }

            writer.println(transaction.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveNewAccount(Account account) {
        final File accountFile = new File(accountsDir.getPath() + "/" + account.getKey());

        try {
            final FileWriter fileWriter = new FileWriter(accountFile);
            fileWriter.write(JsonUtil.toJson(account));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAccount(Account account) {
        final File accountFile = new File(accountsDir.getPath() + "/" + account.getKey());

        if (accountFile.delete()) {
            System.out.printf("Account no. %s has been closed.%n", account.getKey());
        }
    }

    public Account loadAccount(File file) throws IOException {
        return JsonUtil.accountFromJson(fileToJsonDocument(file));
    }

    public String fileName(LocalDateTime t) {
        return String.format("%s-%s-%s.csv", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }

    public File[] allFilesInDir(File directory) {
        return Objects.requireNonNull(directory.listFiles());
    }

    public JSONDocument fileToJsonDocument(File f) throws IOException {
        final FileReader fileReader = new FileReader(f);
        final JSONStreamReaderImpl jsonStreamReader = new JSONStreamReaderImpl(fileReader);
        final JSONDocument document = jsonStreamReader.build();

        fileReader.close();
        jsonStreamReader.close();

        return document;
    }
}
