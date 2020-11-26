package main;

import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppRoot {

    private final static String MAIN_FXML = "io/main/main.fxml";
    private final static String BLACKJACK_FXML = "io/blackjack/blackjack.fxml";
    private final static String BET_FXML = "io/bet/bet.fxml";
    private static final Map<String, Parent> layoutMap = new HashMap<>();
    private static Scene scene;

    public AppRoot(Stage stage) {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(MAIN_FXML));
        FXMLLoader betLoader = new FXMLLoader(getClass().getResource(BET_FXML));
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource(BLACKJACK_FXML));

        try {
            mainLoader.load();
            betLoader.load();
            gameLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        layoutMap.put("home", mainLoader.getRoot());
        layoutMap.put("bet", betLoader.getRoot());
        layoutMap.put("game", gameLoader.getRoot());

        scene = new Scene(layoutMap.get("home"));
        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.show();
    }

    public static void switchToBetScreen() {
        scene.setRoot(layoutMap.get("bet"));
    }

    public static void switchToBlackjackScreen() {
        scene.setRoot(layoutMap.get("game"));
    }
}
