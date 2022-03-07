package main;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.injection.BaseInjectionModule;
import main.io.injection.ConfigInjectionModule;
import main.io.injection.FXMLInjectionModule;
import main.io.log.GameLogger;
import main.io.registration.RegistrationController;
import main.io.storage.AccountStorage;
import main.io.storage.Directory;
import main.io.storage.FileSystem;
import main.usecase.*;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.EventNetwork;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import static com.google.inject.Guice.createInjector;

public class AppRoot {

    public AppRoot(Stage stage,
                   Map<Directory, File> directoryMap,
                   Map<Layout, FXMLLoader> resourceMap,
                   FileSystem fileSystem) {

        final Injector configInjector = createInjector(new ConfigInjectionModule(fileSystem));
        final Injector baseInjector = createInjector(new BaseInjectionModule(directoryMap));
        final Injector fxmlInjection = createInjector(new FXMLInjectionModule(stage, baseInjector, resourceMap));

        final Game game = configInjector.getInstance(Game.class);

        final AccountStorage accountStorage = baseInjector.getInstance(AccountStorage.class);
        final GameLogger gameLogger = baseInjector.getInstance(GameLogger.class);
        final AccountCache accountCache = baseInjector.getInstance(AccountCache.class);
        final TransactionCache transactionCache = baseInjector.getInstance(TransactionCache.class);
        final Transactor transactor = baseInjector.getInstance(Transactor.class);
        final EventNetwork eventNetwork = baseInjector.getInstance(EventNetwork.class);

        final LayoutManager layoutManager = fxmlInjection.getInstance(LayoutManager.class);
        final HomeController homeController = fxmlInjection.getInstance(HomeController.class);
        final HistoryController historyController = fxmlInjection.getInstance(HistoryController.class);
        final BlackjackController blackjackController = fxmlInjection.getInstance(BlackjackController.class);
        final BetController betController = fxmlInjection.getInstance(BetController.class);
        final RegistrationController registrationController = fxmlInjection.getInstance(RegistrationController.class);

        final Collection<EventConnection> eventConnections = new LinkedList<EventConnection>() {{
            add(accountCache);
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

        layoutManager.initializeLayout();

        new Thread(accountStorage::loadAllAccounts, "Account Load Thread").start();
        new Thread(accountStorage::loadAllTransactions, "Transaction Load Thread").start();
    }
}
