package main;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.injection.FXMLInjectionModule;
import main.io.injection.BaseInjectionModule;
import main.io.log.GameLogger;
import main.io.registration.RegistrationController;
import main.io.storage.AccountStorage;
import main.io.storage.Directory;
import main.usecase.*;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.EventNetwork;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import static com.google.inject.Guice.createInjector;
import static java.lang.Thread.currentThread;
import static java.util.UUID.randomUUID;
import static main.usecase.Layout.HOME;

public class AppRoot {

    public static Stage stage;

    public AppRoot(Stage stage, Map<Directory, File> directoryMap, Map<Layout, FXMLLoader> resourceMap) {

        AppRoot.stage = stage;

        currentThread().setName("Trunk thread");

        final Injector injector = createInjector(new BaseInjectionModule(directoryMap));
        final Injector fxmlInjection = createInjector(new FXMLInjectionModule(injector, resourceMap));

        final AccountStorage accountStorage = injector.getInstance(AccountStorage.class);
        final Game game = injector.getInstance(Game.class);
        final GameLogger gameLogger = injector.getInstance(GameLogger.class);
        final LayoutManager layoutManager = fxmlInjection.getInstance(LayoutManager.class);
        final Scene scene = fxmlInjection.getInstance(Scene.class);
        final SelectionMemory selectionMemory = injector.getInstance(SelectionMemory.class);
        final TransactionCache transactionCache = injector.getInstance(TransactionCache.class);
        final Transactor transactor = injector.getInstance(Transactor.class);
        final HomeController homeController = fxmlInjection.getInstance(HomeController.class);
        final HistoryController historyController = fxmlInjection.getInstance(HistoryController.class);
        final BlackjackController blackjackController = fxmlInjection.getInstance(BlackjackController.class);
        final BetController betController = fxmlInjection.getInstance(BetController.class);
        final RegistrationController registrationController = fxmlInjection.getInstance(RegistrationController.class);
        final EventNetwork eventNetwork = new EventNetwork(randomUUID());


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
            add(transactionCache);
            add(game);
            add(gameLogger);
        }};

        eventNetwork.registerListeners(eventConnections);
        eventConnections.forEach(lst ->lst.connectTo(eventNetwork));

        layoutManager.onChangeLayout(HOME);

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();

        new Thread(accountStorage::loadAllAccounts, "Account Load Thread").start();
        new Thread(accountStorage::loadAllTransactions, "Transaction Load Thread").start();
    }
}
