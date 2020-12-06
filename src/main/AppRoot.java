package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.domain.Card;
import main.domain.Suit;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.blackjack.ImageMap;
import main.io.home.HomeController;
import main.usecase.Round;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AppRoot {

    private final static String MAIN_FXML = "io/home/HomeView.fxml";
    private final static String BLACKJACK_FXML = "io/blackjack/BlackjackView.fxml";
    private final static String BET_FXML = "io/bet/BetView.fxml";

    public AppRoot(Stage stage) {
        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource(MAIN_FXML));
        FXMLLoader betLoader = new FXMLLoader(getClass().getResource(BET_FXML));
        FXMLLoader blackjackLoader = new FXMLLoader(getClass().getResource(BLACKJACK_FXML));

        try {
            homeLoader.load();
            betLoader.load();
            blackjackLoader.load();
            ImageMap.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stack<Card> deck = new Stack<Card>() {{
            add(new Card(13, Suit.CLUBS));
            add(new Card(11, Suit.HEARTS));
            add(new Card(10, Suit.HEARTS));
            add(new Card(9, Suit.HEARTS));
            add(new Card(6, Suit.CLUBS));
            add(new Card(3, Suit.SPADES));
            add(new Card(6, Suit.CLUBS));
            add(new Card(5, Suit.HEARTS));
            add(new Card(7, Suit.HEARTS));
            add(new Card(9, Suit.DIAMONDS));
            add(new Card(8, Suit.SPADES));
            add(new Card(4, Suit.CLUBS));
            add(new Card(1, Suit.HEARTS));
            add(new Card(13, Suit.DIAMONDS));
            add(new Card(11, Suit.CLUBS));
            add(new Card(9, Suit.CLUBS));
            add(new Card(1, Suit.CLUBS));
            add(new Card(10, Suit.CLUBS));
        }};
        //Game game = new Game(200, shuffle(fresh()));
        Round round = new Round(deck);

        HomeController homeController = homeLoader.getController();
        BlackjackController blackjackController = blackjackLoader.getController();
        BetController betController = betLoader.getController();

        Map<Layout, Parent> layoutMap = new HashMap<>();
        layoutMap.put(Layout.HOME, homeLoader.getRoot());
        layoutMap.put(Layout.BET, betLoader.getRoot());
        layoutMap.put(Layout.GAME, blackjackLoader.getRoot());

        Scene scene = new Scene(layoutMap.get(Layout.HOME));
        LayoutManager layoutManager = new LayoutManager(scene, layoutMap);

        round.registerGameStateListener(betController);
        round.registerGameStateListener(blackjackController);
        round.registerGameStateListener(homeController);
        round.registerGameStateListener(layoutManager);

        homeController.registerControlListener(round);
        homeController.registerNavListener(round);
        betController.registerControlListener(round);
        betController.registerNavListener(round);
        blackjackController.registerControlListener(round);
        blackjackController.registerNavListener(round);

        homeController.registerNavListener(layoutManager);
        betController.registerNavListener(layoutManager);
        blackjackController.registerNavListener(layoutManager);

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.show();
    }
}
