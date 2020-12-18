package main.io.storage;

import main.domain.Account;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.*;

public class SaveFile {

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();

        try {
            Scanner sc = new Scanner(new File(SaveFile.class.getResource("/accounts/accounts.csv").toURI()));
            sc.useDelimiter("\n");
            sc.next();

            while (sc.hasNext()) {
                String[] line = sc.next().split(",");
                accounts.add(new Account(UUID.fromString(line[0]), line[1], Integer.parseInt(line[2])));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not load accounts file!");
            return accounts;
        } catch ( NoSuchElementException | URISyntaxException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }

        return accounts;
    }
}
