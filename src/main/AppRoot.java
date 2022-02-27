package main;

import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.domain.Card;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.io.ResourceLoader;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.blackjack.ImageMap;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.log.GameLogger;
import main.io.registration.RegistrationController;
import main.io.storage.AccountStorage;
import main.io.storage.FileSystem;
import main.usecase.*;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.EventNetwork;

import java.util.*;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static java.lang.Thread.currentThread;
import static java.util.UUID.randomUUID;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.domain.Evaluate.transactionEvaluators;
import static main.io.ResourceLoader.*;
import static main.usecase.Layout.*;
import static main.usecase.eventing.Predicate.ACCOUNT_SELECTED;
import static main.usecase.eventing.Predicate.TRANSACTION;

public class AppRoot {

    public static Stage stage;

    public AppRoot(Stage stage, Injector injector) {

        AppRoot.stage = stage;

        currentThread().setName("Trunk thread");

        /*
         * Load images and config from disk.
         * These are not event listeners.
         */
        ImageMap.load();
        ResourceLoader.load();

        final SelectionMemory selectionMemory = injector.getInstance(SelectionMemory.class);
        final AccountStorage accountStorage = injector.getInstance(AccountStorage.class);
        final GameLogger gameLogger = injector.getInstance(GameLogger.class);
        final FileSystem fileSystem = injector.getInstance(FileSystem.class);

        final Properties config = fileSystem.loadConfig();
        final String deckName = (String) config.get("game.deckName");
        final int numDecks = parseInt((String) config.get("game.numDecks"));
        final Stack<Card> deck = deckName.equals("default") ? freshlyShuffledDeck(numDecks) : fileSystem.loadDeck(deckName);
        final Map<Layout, Parent> layoutMap = loadLayoutMap();
        final Scene scene = new Scene(layoutMap.get(HOME));
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = transactionEvaluators();

        /*
         * These are event listeners
         */
        final LayoutManager layoutManager = new LayoutManager(randomUUID(), stage, scene, layoutMap);
        final TransactionMemory transactionMemory = new TransactionMemory(randomUUID(), new TreeMap<>());

        final HomeController homeController = (HomeController) loadController(HOME);
        final BlackjackController blackjackController = (BlackjackController) loadController(GAME);
        final BetController betController = (BetController) loadController(BET);
        final HistoryController historyController = (HistoryController) loadController(HISTORY);
        final RegistrationController registrationController = (RegistrationController) loadController(REGISTRATION);

        /*
         * Wire everything up
         */

        final Collection<EventConnection> eventConnections = new LinkedList<EventConnection>() {{
            add(selectionMemory); // TODO: monitor this after switching to multi-threading.
            add(homeController);
            add(historyController);
            add(blackjackController);
            add(betController);
            add(registrationController);
            add(layoutManager);
            add(accountStorage);
            add(new Transactor(randomUUID(), evaluators));
            add(transactionMemory);
            add(new Game(randomUUID(), deck, numDecks));
        }};

        final EventNetwork eventNetwork = new EventNetwork(randomUUID(), eventConnections);

        eventNetwork.registerResponder(ACCOUNT_SELECTED, selectionMemory);
        eventNetwork.registerResponder(TRANSACTION, transactionMemory);

        eventNetwork.registerGameStateListener(gameLogger);
        eventNetwork.registerTransactionListener(gameLogger);
        eventNetwork.registerAccountListener(gameLogger);

        eventConnections.forEach(lst ->lst.connectTo(eventNetwork));

        /*
         * Initialize JavaFX Stage
         */

        layoutManager.onChangeLayout(HOME);

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();

        // Load accounts and transactions from disk
        new Thread(accountStorage::loadAllAccounts, "Account Load Thread").start();
        new Thread(accountStorage::loadAllTransactions, "Transaction Load Thread").start();
    }
}
