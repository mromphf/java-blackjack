package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.adapter.blackjack.ImageMap;
import main.adapter.storage.FileSystem;

import static java.lang.Thread.currentThread;
import static main.adapter.storage.FileSystem.directoryMap;
import static main.adapter.storage.FileSystem.resourceMap;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new AppRoot(stage, directoryMap(), resourceMap(), new FileSystem(directoryMap()));
    }

    public static void main(String[] args) {
        currentThread().setName("Main Thread");
        ImageMap.load();
        launch(args);
    }
}
