package main;

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
import main.io.log.ConsoleLogHandler;
import main.io.log.FileLogHandler;
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
import static main.usecase.Layout.*;
import static main.usecase.eventing.Predicate.ACCOUNT_SELECTED;
import static main.usecase.eventing.Predicate.TRANSACTION;

public class AppRoot {

    public static Stage stage;

    public AppRoot(Stage stage) {

        AppRoot.stage = stage;

        currentThread().setName("Trunk thread");

        /*
         * Load images and config from disk.
         * These are not event listeners.
         */
        ImageMap.load();

        final ResourceLoader loader = new ResourceLoader();
        final FileSystem memory = new FileSystem(loader.getDirectoryMap());
        final Properties config = memory.loadConfig();
        final String deckName = (String) config.get("game.deckName");
        final int numDecks = parseInt((String) config.get("game.numDecks"));
        final Stack<Card> deck = deckName.equals("default") ? freshlyShuffledDeck(numDecks) : memory.loadDeck(deckName);
        final FileLogHandler fileLogHandler = new FileLogHandler();
        final ConsoleLogHandler consoleLogHandler = new ConsoleLogHandler();
        final Map<Layout, Parent> layoutMap = loader.loadLayoutMap();
        final Scene scene = new Scene(layoutMap.get(HOME));
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = transactionEvaluators();

        /*
         * These are event listeners
         */
        final Transactor transactor = new Transactor(randomUUID(), evaluators);
        final SelectionMemory selectionMemory = new SelectionMemory(randomUUID(), new TreeMap<>());
        final Game game = new Game(randomUUID(), deck, numDecks);
        final GameLogger gameLogger = new GameLogger(randomUUID(), "Game Logger", null);
        final AccountStorage accountStorage = new AccountStorage(randomUUID(), memory, memory);
        final LayoutManager layoutManager = new LayoutManager(randomUUID(), stage, scene, layoutMap);
        final TransactionMemory transactionMemory = new TransactionMemory(randomUUID(), new TreeMap<>());

        final HomeController homeController = (HomeController) loader.loadController(HOME);
        final BlackjackController blackjackController = (BlackjackController) loader.loadController(GAME);
        final BetController betController = (BetController) loader.loadController(BET);
        final HistoryController historyController = (HistoryController) loader.loadController(HISTORY);
        final RegistrationController registrationController = (RegistrationController) loader.loadController(REGISTRATION);

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
            add(transactor);
            add(transactionMemory);
            add(game);
        }};

        final EventNetwork eventNetwork = new EventNetwork(randomUUID(), eventConnections);

        eventNetwork.registerResponder(ACCOUNT_SELECTED, selectionMemory);
        eventNetwork.registerResponder(TRANSACTION, transactionMemory);

        eventNetwork.registerGameStateListener(gameLogger);
        eventNetwork.registerTransactionListener(gameLogger);
        eventNetwork.registerAccountListener(gameLogger);

        eventConnections.forEach(lst ->lst.connectTo(eventNetwork));

        gameLogger.addHandler(consoleLogHandler);
        gameLogger.addHandler(fileLogHandler);
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
        new Thread(accountStorage::loadAllAccounts, "Data Load Thread").start();
    }
}
