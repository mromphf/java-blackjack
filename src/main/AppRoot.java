package main;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.io.EventListener;
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
import main.usecase.*;
import main.usecase.LayoutManager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import static main.usecase.Layout.*;
import static main.domain.Deck.fresh;
import static main.domain.Deck.shuffle;

public class AppRoot {

    public AppRoot(Stage stage) {
        /*
         * These are not event listeners
         */
        final ResourceLoader loader = new ResourceLoader();
        final SaveFile saveFile = new SaveFile();
        final ConsoleLogHandler consoleLogHandler = new ConsoleLogHandler();
        final Map<Layout, Parent> layoutMap = loader.loadLayoutMap();
        final GameLogger gameLogger = new GameLogger("Game Logger", null);
        final EventNetwork eventNetwork = new EventNetwork();
        final Scene scene = new Scene(layoutMap.get(HOME));

        /*
         * These are event listeners
         */
        final Transactor transactor = new Transactor();
        final Game game = new Game(shuffle(fresh()));
        final AccountStorage accountStorage = new AccountStorage(saveFile);
        final LayoutManager layoutManager = new LayoutManager(scene, layoutMap);
        final HomeController homeController = (HomeController) loader.loadController(HOME);
        final BlackjackController blackjackController = (BlackjackController) loader.loadController(GAME);
        final BetController betController = (BetController) loader.loadController(BET);
        final HistoryController historyController = (HistoryController) loader.loadController(HISTORY);


        /*
         * Wire everything up
         */

        final Collection<EventListener> eventListeners = new LinkedList<EventListener>() {{
            add(homeController);
            add(historyController);
            add(blackjackController);
            add(betController);
            add(layoutManager);
            add(accountStorage);
            add(game);
            add(transactor);
        }};

        final Collection<GameStateListener> gameStateListeners = new LinkedList<GameStateListener>() {{
            add(blackjackController);
            add(transactor);
            add(gameLogger);
        }};

        final Collection<AccountListener> accountListeners = new LinkedList<AccountListener>() {{
            add(accountStorage);
        }};

        final Collection<BalanceListener> balanceListeners = new LinkedList<BalanceListener>() {{
            add(homeController);
            add(betController);
            add(blackjackController);
            add(layoutManager);
            add(gameLogger);
        }};

        final Collection<ActionListener> actionListeners = new LinkedList<ActionListener>() {{
            add(game);
            add(transactor);
        }};

        final Collection<MemoryListener> memoryListeners = new LinkedList<MemoryListener>() {{
            add(homeController);
            add(historyController);
        }};

        final Collection<NavListener> navListeners = new LinkedList<NavListener>() {{
            add(game);
            add(layoutManager);
            add(transactor);
            add(historyController);
        }};

        final Collection<TransactionListener> transactionListeners = new LinkedList<TransactionListener>() {{
            add(accountStorage);
            add(historyController);
        }};

        // If this doesn't happen, prepare for NullPointerExceptions (there must be a better way?)
        eventListeners.forEach(lst -> lst.connectTo(eventNetwork));

        gameStateListeners.forEach(eventNetwork::registerListener);
        accountListeners.forEach(eventNetwork::registerListener);
        actionListeners.forEach(eventNetwork::registerListener);
        balanceListeners.forEach(eventNetwork::registerListener);
        memoryListeners.forEach(eventNetwork::registerListener);
        navListeners.forEach(eventNetwork::registerListener);
        transactionListeners.forEach(eventNetwork::registerListener);

        gameLogger.addHandler(consoleLogHandler);

        /*
         * Load from disk
         */

        accountStorage.loadAllAccounts();
        accountStorage.loadAllTransactions();
        ImageMap.load();

        /*
         * Initialize JavaFX Stage
         */

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.show();
    }
}
