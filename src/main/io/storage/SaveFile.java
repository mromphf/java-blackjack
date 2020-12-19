package main.io.storage;

import com.oracle.javafx.jmx.json.JSONDocument;
import com.oracle.javafx.jmx.json.impl.JSONStreamReaderImpl;
import main.domain.Account;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class SaveFile implements Storage {

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
    public void saveNewAccount(Account account) {
        final URL url = SaveFile.class.getResource("/accounts/");
        final File accountFile = new File(url.getPath() + account.getKey());

        try {
            if (accountFile.createNewFile()) {
                final FileWriter fileWriter = new FileWriter(accountFile);
                final JSONDocument document = JSONDocument.createObject();

                document.setString("key", account.getKey().toString());
                document.setString("name", account.getName());
                document.setString("created", account.getCreated().toString());
                document.setNumber("balance", account.getBalance());

                fileWriter.write(document.toString());
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Account loadAccount(File file) throws IOException {
        final FileReader fileReader = new FileReader(file);
        final JSONStreamReaderImpl jsonStreamReader = new JSONStreamReaderImpl(fileReader);
        final JSONDocument document = jsonStreamReader.build();

        fileReader.close();
        jsonStreamReader.close();

        return new Account(
                UUID.fromString(document.getString("key")),
                document.getString("name"),
                document.getNumber("balance").intValue(),
                LocalDateTime.parse(document.getString("created"))
        );
    }
}
