package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.io.ResourceLoader;
import main.io.blackjack.ImageMap;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new AppRoot(stage);
    }

    public static void main(String[] args) {
        ImageMap.load();
        ResourceLoader.load();
        launch(args);
    }
}
