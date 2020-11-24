package blackjack;

import blackjack.io.bet.BetController;
import blackjack.io.game.GameController;
import blackjack.io.main.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private final static String MAIN_FXML = "io/main/main.fxml";
    private final static String BLACKJACK_FXML = "io/game/blackjack.fxml";
    private final static String BET_FXML = "io/bet/bet.fxml";

    private Scene scene;
    private FXMLLoader gameLoader;
    private FXMLLoader betLoader;

    @Override
    public void start(Stage stage) {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(MAIN_FXML));
        betLoader = new FXMLLoader(getClass().getResource(BET_FXML));
        gameLoader = new FXMLLoader(getClass().getResource(BLACKJACK_FXML));

        try {
            new MainController(this, mainLoader);
            new BetController(this, betLoader);
            new GameController(gameLoader);
            scene = new Scene(mainLoader.getRoot());
            stage.setScene(scene);
            stage.setTitle("Blackjack");
            stage.setFullScreen(true);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException exception) {
            System.out.print("I couldn't load FXML from specified location! Quitting...");
            System.exit(1);
        }
    }

    public void switchToBetScreen() {
        scene.setRoot(betLoader.getRoot());
    }

    public void switchToBlackjackScreen() {
        scene.setRoot(gameLoader.getRoot());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
