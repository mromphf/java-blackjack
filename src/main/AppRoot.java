package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.blackjack.ImageMap;
import main.io.home.HomeController;
import main.io.log.GameLogger;
import main.io.log.ConsoleLogHandler;
import main.io.storage.AccountStorage;
import main.io.storage.SaveFile;
import main.usecase.Game;
import main.usecase.Transactor;

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

        HomeController homeController = homeLoader.getController();
        BlackjackController blackjackController = blackjackLoader.getController();
        BetController betController = betLoader.getController();

        Map<Layout, Parent> layoutMap = new HashMap<>();
        layoutMap.put(Layout.HOME, homeLoader.getRoot());
        layoutMap.put(Layout.BET, betLoader.getRoot());
        layoutMap.put(Layout.GAME, blackjackLoader.getRoot());

        AccountStorage accountStorage = new AccountStorage(new SaveFile());
        Game game = new Game(shuffle(fresh()));
        Scene scene = new Scene(layoutMap.get(Layout.HOME));
        LayoutManager layoutManager = new LayoutManager(scene, layoutMap);
        Transactor transactor = new Transactor();
        ConsoleLogHandler consoleLogHandler = new ConsoleLogHandler();

        GameLogger gameLogger = new GameLogger("Game Logger", null);
        gameLogger.addHandler(consoleLogHandler);

        game.registerGameStateListener(blackjackController);
        game.registerGameStateListener(transactor);
        game.registerGameStateListener(gameLogger);

        accountStorage.registerMemoryListener(homeController);

        homeController.registerControlListener(game);
        homeController.registerNavListener(game);
        homeController.registerNavListener(layoutManager);
        homeController.registerNavListener(transactor);
        homeController.registerAccountListener(accountStorage);

        betController.registerControlListener(game);
        betController.registerNavListener(game);
        betController.registerNavListener(layoutManager);
        betController.registerNavListener(transactor);

        blackjackController.registerControlListener(game);
        blackjackController.registerNavListener(game);
        blackjackController.registerNavListener(layoutManager);
        blackjackController.registerNavListener(transactor);

        transactor.registerSettlementListener(homeController);
        transactor.registerSettlementListener(betController);
        transactor.registerSettlementListener(blackjackController);
        transactor.registerSettlementListener(layoutManager);
        transactor.registerSettlementListener(gameLogger);

        accountStorage.loadAllAccounts();

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.show();
    }
}
