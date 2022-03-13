package main.adapter.storage;

import javafx.fxml.FXMLLoader;
import main.adapter.injection.BaseInjectionModule;
import main.domain.Deck;
import main.usecase.Layout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static java.lang.System.exit;
import static main.adapter.storage.Directory.*;
import static main.adapter.storage.FileFunctions.fileToJson;
import static main.adapter.storage.JsonUtil.deckFromJson;
import static main.usecase.Layout.*;

public class FileSystem {

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

    public Deck loadDeck(String name) {
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
            put(LOG, new File("./log/"));
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
}
