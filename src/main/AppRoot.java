package main;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.io.ResourceLoader;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.blackjack.ImageMap;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.log.ConsoleLogHandler;
import main.io.log.GameLogger;
import main.io.storage.AccountStorage;
import main.io.storage.SaveFile;
import main.usecase.Game;
import main.usecase.Layout;
import main.usecase.LayoutManager;
import main.usecase.Transactor;

import java.util.Map;

import static main.usecase.Layout.*;
import static main.domain.Deck.fresh;
import static main.domain.Deck.shuffle;

public class AppRoot {

    public AppRoot(Stage stage) {
        final ResourceLoader loader = new ResourceLoader();
        final SaveFile saveFile = new SaveFile();
        final Transactor transactor = new Transactor();
        final ConsoleLogHandler consoleLogHandler = new ConsoleLogHandler();
        final GameLogger gameLogger = new GameLogger("Game Logger", null);
        final Game game = new Game(shuffle(fresh()));

        final Map<Layout, Parent> layoutMap = loader.loadLayoutMap();
        final AccountStorage accountStorage = new AccountStorage(saveFile);
        final Scene scene = new Scene(layoutMap.get(HOME));
        final LayoutManager layoutManager = new LayoutManager(scene, layoutMap);

        final HomeController homeController = (HomeController) loader.loadController(HOME);
        final BlackjackController blackjackController = (BlackjackController) loader.loadController(GAME);
        final BetController betController = (BetController) loader.loadController(BET);
        final HistoryController historyController = (HistoryController) loader.loadController(HISTORY);

        gameLogger.addHandler(consoleLogHandler);

        game.registerGameStateListener(blackjackController);
        game.registerGameStateListener(transactor);
        game.registerGameStateListener(gameLogger);

        accountStorage.registerMemoryListener(homeController);
        accountStorage.registerMemoryListener(historyController);

        historyController.registerNavListener(layoutManager);

        homeController.registerNavListener(game);
        homeController.registerNavListener(layoutManager);
        homeController.registerNavListener(transactor);
        homeController.registerNavListener(historyController);
        homeController.registerAccountListener(accountStorage);

        betController.registerActionListener(game);
        betController.registerActionListener(transactor);
        betController.registerNavListener(game);
        betController.registerNavListener(layoutManager);

        blackjackController.registerActionListener(game);
        blackjackController.registerNavListener(game);
        blackjackController.registerNavListener(layoutManager);
        blackjackController.registerNavListener(transactor);

        transactor.registerBalanceListener(homeController);
        transactor.registerBalanceListener(betController);
        transactor.registerBalanceListener(blackjackController);
        transactor.registerBalanceListener(layoutManager);
        transactor.registerBalanceListener(gameLogger);
        transactor.registerTransactionListener(historyController);
        transactor.registerTransactionListener(accountStorage);

        accountStorage.loadAllAccounts();
        accountStorage.loadAllTransactions();
        ImageMap.load();

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.show();
    }
}
