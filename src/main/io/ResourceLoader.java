package main.io;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.io.storage.Directory;
import main.usecase.Layout;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.lang.System.getenv;
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

    private final static Map<Directory, File> directoryMap = new HashMap<>();

    public ResourceLoader() {
        //TODO: Fix the csv files being in here -- Aug 5 2021
        directoryMap.put(ACCOUNTS, new File("./app-data/accounts-grouped/accounts-grouped.csv"));
        directoryMap.put(ACCOUNTS_CLOSED, new File("./app-data/accounts-closed/account-closures-bundled.csv"));
        directoryMap.put(DECKS, new File("./app-data/decks/"));
        directoryMap.put(LOG, new File("./app-data/log/"));
        directoryMap.put(TRANSACTIONS, new File("./app-data/transactions-grouped/"));

        resourceMap.keySet().forEach(k -> {
            try {
                resourceMap.get(k).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Map<Layout, Parent> loadLayoutMap() {
        return resourceMap.keySet()
                .stream()
                .collect(Collectors.toMap(l -> l, this::loadRoot));
    }

    public Map<Directory, File> getDirectoryMap() {
        return directoryMap;
    }

    public Object loadController(Layout layout) {
        return resourceMap.get(layout).getController();
    }

    public Parent loadRoot(Layout layout) {
        return resourceMap.get(layout).getRoot();
    }

    public Connection loadDbConnection() {
        final String url = getenv("PSQL_URL");
        final String username = getenv("PSQL_USERNAME");
        final Properties props = new Properties();

        props.setProperty("user", username);

        try {
            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
