package main.io;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.usecase.Layout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static main.usecase.Layout.*;

public class ResourceLoader {

    private final static Map<Layout, FXMLLoader> resourceMap = new HashMap<Layout, FXMLLoader>() {{
        put(HOME, new FXMLLoader(ResourceLoader.class.getResource("home/HomeView.fxml")));
        put(BET, new FXMLLoader(ResourceLoader.class.getResource("bet/BetView.fxml")));
        put(GAME, new FXMLLoader(ResourceLoader.class.getResource("blackjack/BlackjackView.fxml")));
        put(HISTORY, new FXMLLoader(ResourceLoader.class.getResource("history/HistoryView.fxml")));
    }};

    public ResourceLoader() {
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

    public Object loadController(Layout layout) {
        return resourceMap.get(layout).getController();
    }

    public Parent loadRoot(Layout layout) {
        return resourceMap.get(layout).getRoot();
    }
}
