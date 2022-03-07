package main.adapter.storage;

import javafx.fxml.FXMLLoader;
import main.adapter.injection.BaseInjectionModule;
import main.common.CsvUtil;
import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;
import main.usecase.Layout;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.exit;
import static main.common.CsvUtil.*;
import static main.common.JsonUtil.deckFromJson;
import static main.adapter.storage.Directory.*;
import static main.adapter.storage.FileFunctions.fileToJson;
import static main.adapter.storage.FileFunctions.readCsvLines;
import static main.usecase.Layout.*;

public class FileSystem implements TransactionMemory, AccountMemory {

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

    public static Map<Directory, File> directoryMap() {
        return new HashMap<Directory, File>() {{
            put(ACCOUNTS, new File("./app-data/accounts-grouped/accounts-grouped.csv"));
            put(ACCOUNTS_CLOSED, new File("./app-data/accounts-closed/account-closures-bundled.csv"));
            put(DECKS, new File("./app-data/decks/"));
            put(LOG, new File("./app-data/log/"));
            put(TRANSACTIONS, new File("./app-data/transactions-grouped/"));
        }};
    }

    public static Map<Layout, FXMLLoader> resourceMap() {
        return new HashMap<Layout, FXMLLoader>() {{
            put(BET, new FXMLLoader(BaseInjectionModule.class.getResource("../ui/bet/BetView.fxml")));
            put(GAME, new FXMLLoader(BaseInjectionModule.class.getResource("../ui/blackjack/BlackjackView.fxml")));
            put(HISTORY, new FXMLLoader(BaseInjectionModule.class.getResource("../ui/history/HistoryView.fxml")));
            put(HOME, new FXMLLoader(BaseInjectionModule.class.getResource("../ui/home/HomeView.fxml")));
            put(REGISTRATION, new FXMLLoader(BaseInjectionModule.class.getResource("../ui/registration/RegistrationView.fxml")));
        }};
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        final File file = inferTransactionsFile(transaction.getAccountKey());
        appendToCsv(file, TRANSACTION_HEADER, toCsvRow(transaction));
    }

    @Override
    public void openNewAccount(Account account) {
        final File file = directories.get(ACCOUNTS);
        appendToCsv(file, ACCOUNT_HEADER, toCsvRow(account));
    }

    @Override
    public void closeAccount(Account account) {
        final File file = directories.get(ACCOUNTS_CLOSED);
        appendToCsv(file, ACCOUNT_CLOSURE_HEADER, accountClosureRow(account));
    }

    @Override
    public Set<Account> loadAllAccounts(Collection<UUID> closedAccountKeys) {
        final File accountsDir = directories.get(ACCOUNTS);

        return readCsvLines(accountsDir).stream()
                .map(line -> line.split(","))
                .map(CsvUtil::accountFromCsvRow)
                .filter(account -> !closedAccountKeys.contains(account.getKey()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Transaction> loadAllTransactions(Collection<Account> openAccounts) {
        return openAccounts.stream()
                .map(Account::getKey)
                .map(key -> format("%s/%s.csv", directories.get(TRANSACTIONS), key))
                .map(File::new)
                .map(FileFunctions::readCsvLines)
                .flatMap(Collection::stream)
                .map(line -> line.split(","))
                .map(CsvUtil::transactionsFromCsvRow)
                .collect(Collectors.toList());
    }

    private File inferTransactionsFile(UUID key) {
        return new File(format("%s/%s.csv", directories.get(TRANSACTIONS).getPath(), key));
    }
}
