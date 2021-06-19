package main;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.domain.Card;
import main.domain.Evaluate;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.io.EventConnection;
import main.io.ResourceLoader;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.blackjack.ImageMap;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.log.ConsoleLogHandler;
import main.io.log.GameLogger;
import main.io.storage.AccountStorage;
import main.io.storage.FileSystem;
import main.usecase.*;
import main.usecase.eventing.EventNetwork;

import java.util.*;
import java.util.function.Function;

import static main.domain.Deck.fresh;
import static main.domain.Deck.shuffle;
import static main.usecase.Layout.*;
import static main.usecase.eventing.Predicate.ACCOUNT_SELECTED;
import static main.usecase.eventing.Predicate.TRANSACTION;

public class AppRoot {

    public AppRoot(Stage stage) {

        /*
         * These are not event listeners.
         * Load config from disk.
         */
        final ResourceLoader loader = new ResourceLoader();
        final FileSystem memory = new FileSystem("./accounts/", "./transactions/", "./decks/");
        final Config config = memory.loadConfig();
        final String deckName = config.deckName;
        final int numDecks = config.numDecks;
        final Stack<Card> deck = deckName.equals("default") ? shuffle(fresh(numDecks)) : memory.loadDeck(deckName);
        final ConsoleLogHandler consoleLogHandler = new ConsoleLogHandler();
        final Map<Layout, Parent> layoutMap = loader.loadLayoutMap();
        final Scene scene = new Scene(layoutMap.get(HOME));
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = new HashSet<>();
        evaluators.add(Evaluate.doubleDownTransactions());
        evaluators.add(Evaluate.insuranceTransactions());
        evaluators.add(Evaluate.outcomeTransactions());
        evaluators.add(Evaluate.splitTransactions());

        /*
         * These are event listeners
         */
        final Transactor transactor = new Transactor(evaluators);
        final Accounting accounting = new Accounting(new TreeMap<>());
        final Game game = new Game(deck, numDecks);
        final GameLogger gameLogger = new GameLogger("Game Logger", null);
        final AccountStorage accountStorage = new AccountStorage(memory);
        final LayoutManager layoutManager = new LayoutManager(scene, layoutMap);
        final TransactionCache transactionCache = new TransactionCache(new TreeMap<>());

        final HomeController homeController = (HomeController) loader.loadController(HOME);
        final BlackjackController blackjackController = (BlackjackController) loader.loadController(GAME);
        final BetController betController = (BetController) loader.loadController(BET);
        final HistoryController historyController = (HistoryController) loader.loadController(HISTORY);

        /*
         * Wire everything up
         */

        final Collection<EventConnection> eventConnections = new LinkedList<EventConnection>() {{
            add(accounting); // Accounting needs to be added first since it holds the current account pointer
            add(homeController);
            add(historyController);
            add(blackjackController);
            add(betController);
            add(layoutManager);
            add(accountStorage);
            add(transactor);
            add(transactionCache);
            add(game);
        }};

        final EventNetwork eventNetwork = new EventNetwork(eventConnections);

        eventNetwork.registerResponder(ACCOUNT_SELECTED, accounting);
        eventNetwork.registerResponder(TRANSACTION, transactionCache);

        eventNetwork.registerGameStateListener(gameLogger);
        eventNetwork.registerTransactionListener(gameLogger);
        eventNetwork.registerAccountListener(gameLogger);
        eventNetwork.registerActionListener(gameLogger);

        // If this doesn't happen, prepare for NullPointerExceptions (there must be a better way?)
        eventConnections.forEach(lst ->lst.connectTo(eventNetwork));

        gameLogger.addHandler(consoleLogHandler);

        /*
         * Load accounts, transactions and images from disk
         */

        accountStorage.loadAllAccounts();
        accountStorage.loadAllTransactions();
        ImageMap.load();

        /*
         * Initialize JavaFX Stage
         */

        layoutManager.onChangeLayout(HOME);

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();
    }
}
