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
    }};

    private final static Map<Directory, File> directoryMap = new HashMap<>();

    public ResourceLoader() {
        directoryMap.put(ACCOUNTS, new File("./accounts/"));
        directoryMap.put(ACCOUNTS_CLOSED, new File("./accounts-closed/"));
        directoryMap.put(DECKS, new File("./decks"));
        directoryMap.put(TRANSACTIONS, new File("./transactions"));

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
}
