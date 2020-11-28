package main;

import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import main.domain.Card;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.home.HomeController;
import main.usecase.Round;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static main.domain.Deck.*;

public class AppRoot {

    private final static String MAIN_FXML = "io/home/HomeView.fxml";
    private final static String BLACKJACK_FXML = "io/blackjack/BlackjackView.fxml";
    private final static String BET_FXML = "io/bet/BetView.fxml";
    private final Map<Layout, Parent> layoutMap = new HashMap<>();
    private final Scene scene;

    public AppRoot(Stage stage) {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(MAIN_FXML));
        FXMLLoader betLoader = new FXMLLoader(getClass().getResource(BET_FXML));
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource(BLACKJACK_FXML));

        Stack<Card> deck = shuffle(fresh());

        Round round = new Round(this, deck, openingHand(deck));

        HomeController homeController = new HomeController(round);
        BetController betController = new BetController(round);
        BlackjackController blackjackController = new BlackjackController(round);

        mainLoader.setController(homeController);
        betLoader.setController(betController);
        gameLoader.setController(blackjackController);

        try {
            mainLoader.load();
            betLoader.load();
            gameLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        layoutMap.put(Layout.HOME, mainLoader.getRoot());
        layoutMap.put(Layout.BET, betLoader.getRoot());
        layoutMap.put(Layout.GAME, gameLoader.getRoot());

        scene = new Scene(layoutMap.get(Layout.HOME));

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.show();
    }

    public void setLayout(Layout layout) {
        scene.setRoot(layoutMap.get(layout));
    }
}
