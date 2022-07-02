package main.adapter.storage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.adapter.injection.BaseInjectionModule;
import main.usecase.Screen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static main.usecase.Screen.*;

public class FileSystem {


    public static Map<Screen, Parent> resourceMap = new HashMap<>();

    public FileSystem(Map<Directory, File> directories) {
        for (File file : directories.values()) {
            if (file.mkdir()) {
                System.out.printf("Created new directory: %s\n", file.getPath());
            }
        }
    }

    public static void loadFXML(Map<Screen, Object> controllerMap) {
        controllerMap.forEach(FileSystem::loadControllerFXML);
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

    private static void loadControllerFXML(Screen screen, Object controller) {
        try {
            final URL resource = BaseInjectionModule.class.getResource(resourcePathMap().get(screen));
            final FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(params -> controller);
            loader.load();
            resourceMap.put(screen, loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
