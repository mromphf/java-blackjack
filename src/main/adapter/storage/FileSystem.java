package main.adapter.storage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.adapter.injection.BaseInjectionModule;
import main.domain.model.Deck;
import main.usecase.Screen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static java.lang.System.exit;
import static main.adapter.storage.Directory.*;
import static main.adapter.storage.FileFunctions.fileToJson;
import static main.adapter.storage.JsonUtil.deckFromJson;
import static main.usecase.Screen.*;

public class FileSystem {

    private final Map<Directory, File> directories;

    public static Map<Screen, Parent> resourceMap = new HashMap<>();

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
            return new Properties();
        }
    }

    public Deck loadDeck(String name) {
        try {
            final File deckFile = new File(format("%s/%s.json", directories.get(DECKS), name));
            return deckFromJson(fileToJson(deckFile));
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
            return new Deck();
        }
    }

    public static void loadFXML(Map<Screen, Object> controllerMap) {
        controllerMap.forEach(FileSystem::loadControllerFXML);
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

    public static Map<Screen, String> resourcePathMap() {
        return new HashMap<Screen, String>() {{
            put(BET, "../ui/bet/BetView.fxml");
            put(GAME, "../ui/blackjack/BlackjackView.fxml");
            put(HISTORY, "../ui/history/HistoryView.fxml");
            put(HOME, "../ui/home/HomeView.fxml");
            put(REGISTRATION, "../ui/registration/RegistrationView.fxml");
        }};
    }


    private static void loadControllerFXML(Screen layout, Object controller) {
        try {
            final URL resource = BaseInjectionModule.class.getResource(resourcePathMap().get(layout));
            final FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(params -> controller);
            loader.load();
            resourceMap.put(layout, loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
