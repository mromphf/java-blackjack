package main;

import com.google.inject.Injector;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.injection.InjectionModule;
import main.io.log.GameLogger;
import main.io.registration.RegistrationController;
import main.io.storage.AccountStorage;
import main.usecase.*;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.EventNetwork;

import java.util.Collection;
import java.util.LinkedList;

import static com.google.inject.Guice.createInjector;
import static java.lang.Thread.currentThread;
import static java.util.UUID.randomUUID;
import static main.usecase.Layout.HOME;
import static main.usecase.eventing.Predicate.ACCOUNT_SELECTED;
import static main.usecase.eventing.Predicate.TRANSACTION;

public class AppRoot {

    public static Stage stage;

    public AppRoot(Stage stage) {

        AppRoot.stage = stage;

        currentThread().setName("Trunk thread");

        final Injector injector = createInjector(new InjectionModule());

        final AccountStorage accountStorage = injector.getInstance(AccountStorage.class);
        final Game game = injector.getInstance(Game.class);
        final GameLogger gameLogger = injector.getInstance(GameLogger.class);
        final LayoutManager layoutManager = injector.getInstance(LayoutManager.class);
        final Scene scene = injector.getInstance(Scene.class);
        final SelectionMemory selectionMemory = injector.getInstance(SelectionMemory.class);
        final TransactionMemory transactionMemory = injector.getInstance(TransactionMemory.class);
        final Transactor transactor = injector.getInstance(Transactor.class);
        final HomeController homeController = injector.getInstance(HomeController.class);
        final HistoryController historyController = injector.getInstance(HistoryController.class);
        final BlackjackController blackjackController = injector.getInstance(BlackjackController.class);
        final BetController betController = injector.getInstance(BetController.class);
        final RegistrationController registrationController = injector.getInstance(RegistrationController.class);

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
            add(gameLogger);
        }};

        final EventNetwork eventNetwork = new EventNetwork(randomUUID(), eventConnections);

        eventNetwork.registerResponder(ACCOUNT_SELECTED, selectionMemory);
        eventNetwork.registerResponder(TRANSACTION, transactionMemory);

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
