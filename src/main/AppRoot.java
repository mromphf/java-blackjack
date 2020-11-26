package main;

import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.main.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static main.domain.Deck.fresh;
import static main.domain.Deck.shuffle;

public class AppRoot {

    private final static String MAIN_FXML = "io/main/main.fxml";
    private final static String BLACKJACK_FXML = "io/blackjack/blackjack.fxml";
    private final static String BET_FXML = "io/bet/bet.fxml";
    private final FXMLLoader gameLoader;
    private final FXMLLoader betLoader;
    private Scene scene;

    public AppRoot(Stage stage) {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(MAIN_FXML));
        betLoader = new FXMLLoader(getClass().getResource(BET_FXML));
        gameLoader = new FXMLLoader(getClass().getResource(BLACKJACK_FXML));

        try {
            new MainController(this, mainLoader);
            new BetController(this, betLoader);
            new BlackjackController(this, gameLoader, shuffle(fresh()));
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
}
