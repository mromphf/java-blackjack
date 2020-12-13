package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.domain.Account;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.blackjack.ImageMap;
import main.io.home.HomeController;
import main.usecase.Game;
import main.usecase.Settlement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        HomeController homeController = homeLoader.getController();
        BlackjackController blackjackController = blackjackLoader.getController();
        BetController betController = betLoader.getController();

        Map<Layout, Parent> layoutMap = new HashMap<>();
        layoutMap.put(Layout.HOME, homeLoader.getRoot());
        layoutMap.put(Layout.BET, betLoader.getRoot());
        layoutMap.put(Layout.GAME, blackjackLoader.getRoot());

        Account account = new Account(UUID.randomUUID(), "StickyJibs", 200);
        Game game = new Game(shuffle(fresh()));
        Scene scene = new Scene(layoutMap.get(Layout.HOME));
        LayoutManager layoutManager = new LayoutManager(scene, layoutMap);
        Settlement settlement = new Settlement(account);

        game.registerGameStateListener(blackjackController);
        game.registerGameStateListener(settlement);

        homeController.registerControlListener(game);
        homeController.registerNavListener(game);
        homeController.registerNavListener(layoutManager);
        homeController.registerNavListener(settlement);

        betController.registerControlListener(game);
        betController.registerNavListener(game);
        betController.registerNavListener(layoutManager);
        betController.registerNavListener(settlement);

        blackjackController.registerControlListener(game);
        blackjackController.registerNavListener(game);
        blackjackController.registerNavListener(layoutManager);
        blackjackController.registerNavListener(settlement);

        settlement.registerSettlementListener(homeController);
        settlement.registerSettlementListener(betController);
        settlement.registerSettlementListener(blackjackController);
        settlement.registerSettlementListener(layoutManager);

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.show();
    }
}
