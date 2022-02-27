package main.io;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.io.storage.Directory;
import main.usecase.Layout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static main.io.storage.Directory.*;
import static main.usecase.Layout.*;

public class ResourceLoader {

    private final static Map<Layout, FXMLLoader> resourceMap = new HashMap<Layout, FXMLLoader>() {{
        put(HOME, new FXMLLoader(ResourceLoader.class.getResource("home/HomeView.fxml")));
        put(BET, new FXMLLoader(ResourceLoader.class.getResource("bet/BetView.fxml")));
        put(GAME, new FXMLLoader(ResourceLoader.class.getResource("blackjack/BlackjackView.fxml")));
        put(HISTORY, new FXMLLoader(ResourceLoader.class.getResource("history/HistoryView.fxml")));
        put(REGISTRATION, new FXMLLoader(ResourceLoader.class.getResource("registration/RegistrationView.fxml")));
    }};

    private final static Map<Directory, File> directoryMap = new HashMap<Directory, File>() {{
        put(ACCOUNTS, new File("./app-data/accounts-grouped/accounts-grouped.csv"));
        put(ACCOUNTS_CLOSED, new File("./app-data/accounts-closed/account-closures-bundled.csv"));
        put(DECKS, new File("./app-data/decks/"));
        put(LOG, new File("./app-data/log/"));
        put(TRANSACTIONS, new File("./app-data/transactions-grouped/"));
    }};

    public static void load() {
        resourceMap.keySet().forEach(k -> {
            try {
                resourceMap.get(k).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static Map<Layout, Parent> loadLayoutMap() {
        return resourceMap.keySet()
                .stream()
                .collect(Collectors.toMap(layout -> layout, ResourceLoader::loadRoot));
    }

    public static Map<Directory, File> getDirectoryMap() {
        return directoryMap;
    }

    public static Object loadController(Layout layout) {
        return resourceMap.get(layout).getController();
    }

    public static Parent loadRoot(Layout layout) {
        return resourceMap.get(layout).getRoot();
    }
}
