package main.io.storage;

import com.oracle.javafx.jmx.json.JSONDocument;
import com.oracle.javafx.jmx.json.impl.JSONStreamReaderImpl;
import main.domain.Account;
import main.domain.Transaction;
import main.io.util.JsonUtil;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class SaveFile implements Memory {

    @Override
    public Set<Account> loadAllAccounts() {
        Set<Account> accounts = new HashSet<>();

        try {
            final URL url = SaveFile.class.getResource("/accounts");
            final File accountsDirectory = new File(url.getPath());
            for (File file : Objects.requireNonNull(accountsDirectory.listFiles())) {
                accounts.add(loadAccount(file));
            }
        } catch (IOException e) {
            System.out.println("Could not load accounts file!");
            return accounts;
        } catch ( NoSuchElementException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }

        return accounts;
    }

    @Override
    public Set<Transaction> loadAllTransactions() {
        Set<Transaction> transactions = new HashSet<>();

        try {
            final URL url = SaveFile.class.getResource("/transactions/2020-12-19.csv");
            final File transactionsFile = new File(url.getPath());
            final Scanner sc = new Scanner(transactionsFile);

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final URL url = SaveFile.class.getResource("/transactions/");
        final String pathToTransactionsDir = new File(url.getPath()).getPath() + "/";
        final String transactionFilename = pathToTransactionsDir + fileName(transaction.getTime());
        final File transactionsFile = new File(transactionFilename);

        try {
            if (transactionsFile.createNewFile()) {
                System.out.printf("Created new file: %s", transactionFilename);
            }

            final PrintWriter writer = new PrintWriter(new FileWriter(transactionsFile, true));
            writer.println(transaction.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveNewAccount(Account account) {
        final URL url = SaveFile.class.getResource("/accounts/");
        final File accountFile = new File(url.getPath() + account.getKey());

        try {
            if (accountFile.createNewFile()) {
                final FileWriter fileWriter = new FileWriter(accountFile);
                fileWriter.write(JsonUtil.toJson(account));
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAccount(Account account) {
        final URL url = SaveFile.class.getResource("/accounts/");
        final File accountFile = new File(url.getPath() + account.getKey());
        if (accountFile.delete()) {
            System.out.printf("Account no. %s has been closed.%n", account.getKey());
        }
    }

    public Account loadAccount(File file) throws IOException {
        final FileReader fileReader = new FileReader(file);
        final JSONStreamReaderImpl jsonStreamReader = new JSONStreamReaderImpl(fileReader);
        final JSONDocument document = jsonStreamReader.build();
        final UUID accountKey = UUID.fromString(document.getString("key"));

        fileReader.close();
        jsonStreamReader.close();

        return JsonUtil.accountFromJson(document).updateBalance(Account.deriveBalance(accountKey, loadAllTransactions()));
    }

    public String fileName(LocalDateTime t) {
        return String.format("%s-%s-%s.csv", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }
}
