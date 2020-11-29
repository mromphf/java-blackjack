package main;

import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.home.HomeController;
import main.usecase.Round;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.domain.Deck.fresh;
import static main.domain.Deck.shuffle;


public class AppRoot {

    private final static String MAIN_FXML = "io/home/HomeView.fxml";
    private final static String BLACKJACK_FXML = "io/blackjack/BlackjackView.fxml";
    private final static String BET_FXML = "io/bet/BetView.fxml";
    private final Map<Layout, Parent> layoutMap = new HashMap<>();
    private final Scene scene;

    public AppRoot(Stage stage) {
        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource(MAIN_FXML));
        FXMLLoader betLoader = new FXMLLoader(getClass().getResource(BET_FXML));
        FXMLLoader blackjackLoader = new FXMLLoader(getClass().getResource(BLACKJACK_FXML));

        try {
            homeLoader.load();
            betLoader.load();
            blackjackLoader.load();
        } catch (IOException e) {
            e.printStackTrace();

        }

        Round round = new Round(this, shuffle(fresh()));

        HomeController homeController = homeLoader.getController();
        BlackjackController blackjackController = blackjackLoader.getController();
        BetController betController = betLoader.getController();

        round.registerGameStateListener(betController);
        round.registerGameStateListener(blackjackController);
        round.registerOutcomeListener(blackjackController);
        homeController.registerControlListener(round);
        betController.registerControlListener(round);
        blackjackController.registerControlListener(round);

        layoutMap.put(Layout.HOME, homeLoader.getRoot());
        layoutMap.put(Layout.BET, betLoader.getRoot());
        layoutMap.put(Layout.GAME, blackjackLoader.getRoot());

        scene = new Scene(layoutMap.get(Layout.HOME));

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.show();
    }

    public void setLayout(Layout layout) {
        scene.setRoot(layoutMap.get(layout));
    }
}
