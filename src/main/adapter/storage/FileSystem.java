package main.adapter.storage;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.adapter.injection.BaseInjectionModule;
import main.adapter.ui.Screen;
import main.adapter.ui.ScreenObserver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;
import static main.adapter.ui.Screen.screensWithPaths;

public class FileSystem {

    private final Map<Screen, ScreenObserver> screenObservers;

    @Inject
    public FileSystem(Map<Screen, ScreenObserver> screenObservers) {

        for (Directory directory : Directory.values()) {

            final File file = new File(directory.path());

            if (file.exists()) {
                System.out.printf("INFO: %s directory exists\n", file.getPath());

            } else if (file.mkdir()) {
                System.out.printf("Created new directory %s\n", file.getPath());

            } else {
                throw new IllegalStateException();
            }
        }

        this.screenObservers = screenObservers;
    }

    public Map<Screen, Parent> loadFXMLDocuments() {
        final Map<Screen, Parent> nodeMap = new HashMap<>();

        for (Screen screen: screensWithPaths()) {

            try {
                final URL resource = BaseInjectionModule.class.getResource(screen.path());
                final FXMLLoader loader = new FXMLLoader(resource);

                loader.setControllerFactory(params -> screenObservers.get(screen));
                loader.load();
                nodeMap.put(screen, loader.getRoot());

            } catch (IOException e) {
                e.printStackTrace();
                exit(1);
            }
        }

        return nodeMap;
    }
}
