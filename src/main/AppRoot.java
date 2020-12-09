package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.blackjack.ImageMap;
import main.io.home.HomeController;
import main.usecase.Game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.domain.Deck.fresh;
import static main.domain.Deck.shuffle;

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

        Game game = new Game(shuffle(fresh()));

        HomeController homeController = homeLoader.getController();
        BlackjackController blackjackController = blackjackLoader.getController();
        BetController betController = betLoader.getController();

        Map<Layout, Parent> layoutMap = new HashMap<>();
        layoutMap.put(Layout.HOME, homeLoader.getRoot());
        layoutMap.put(Layout.BET, betLoader.getRoot());
        layoutMap.put(Layout.GAME, blackjackLoader.getRoot());

        Scene scene = new Scene(layoutMap.get(Layout.HOME));
        LayoutManager layoutManager = new LayoutManager(scene, layoutMap);

        game.registerGameStateListener(betController);
        game.registerGameStateListener(blackjackController);
        game.registerGameStateListener(homeController);
        game.registerGameStateListener(layoutManager);

        homeController.registerControlListener(game);
        homeController.registerNavListener(game);
        betController.registerControlListener(game);
        betController.registerNavListener(game);
        blackjackController.registerControlListener(game);
        blackjackController.registerNavListener(game);

        homeController.registerNavListener(layoutManager);
        betController.registerNavListener(layoutManager);
        blackjackController.registerNavListener(layoutManager);

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.show();
    }
}
