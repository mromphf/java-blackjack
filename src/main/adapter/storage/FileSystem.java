package main.adapter.storage;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.adapter.injection.BaseInjectionModule;
import main.usecase.Screen;
import main.usecase.ScreenObserver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;
import static main.usecase.Screen.*;

public class FileSystem {

    private final Map<Screen, ScreenObserver> screenObservers;

    @Inject
    public FileSystem(Map<Screen, ScreenObserver> screenObservers) {

        for (Directory directory : Directory.values()) {
            final File file = new File(directory.path());
            if (file.exists()) {
                System.out.printf("INFO: %s directory exists\n", file.getPath());
            } else if (file.mkdir()) {
                System.out.printf("Created new directory: %s\n", file.getPath());
            } else {
                throw new RuntimeException();
            }
        }

        this.screenObservers = screenObservers;
    }

    public Map<Screen, Parent> loadFXML() {
        return loadControllerFXML();
    }

    private Map<Screen, String> resourcePathMap() {
        return new HashMap<Screen, String>() {{
            put(BET, "../ui/bet/BetView.fxml");
            put(GAME, "../ui/blackjack/BlackjackView.fxml");
            put(HISTORY, "../ui/history/HistoryView.fxml");
            put(HOME, "../ui/home/HomeView.fxml");
            put(REGISTRATION, "../ui/registration/RegistrationView.fxml");
        }};
    }

    private Map<Screen, Parent> loadControllerFXML() {
        final Map<Screen, Parent> nodeMap = new HashMap<>();

        for (Map.Entry<Screen, String> path : resourcePathMap().entrySet()) {

            try {
                final URL resource = BaseInjectionModule.class.getResource(resourcePathMap().get(path.getKey()));
                final FXMLLoader loader = new FXMLLoader(resource);

                loader.setControllerFactory(params -> screenObservers.get(path.getKey()));
                loader.load();
                nodeMap.put(path.getKey(), loader.getRoot());

            } catch (IOException e) {
                e.printStackTrace();
                exit(1);
            }
        }

        return nodeMap;
    }
}
