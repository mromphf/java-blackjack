package main;

import com.google.inject.Injector;
import com.google.inject.Module;
import javafx.application.Application;
import javafx.stage.Stage;
import main.adapter.injection.BaseInjectionModule;
import main.adapter.log.GameLogger;
import main.adapter.ui.bet.BetController;
import main.adapter.ui.blackjack.BlackjackController;
import main.adapter.ui.blackjack.ImageMap;
import main.adapter.ui.history.HistoryController;
import main.adapter.ui.home.HomeController;
import main.adapter.ui.registration.RegistrationController;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.inject.Guice.createInjector;
import static java.lang.Thread.currentThread;
import static main.adapter.storage.FileSystem.loadFXML;
import static main.adapter.storage.FileSystem.resourceMap;
import static main.usecase.Screen.*;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        final Module baseInjectionModule = new BaseInjectionModule();

        final Injector injector = createInjector(baseInjectionModule);
        final TransactionService transactionService = injector.getInstance(TransactionService.class);
        final AccountService accountService = injector.getInstance(AccountService.class);
        final ScreenSupervisor screenSupervisor = injector.getInstance(ScreenSupervisor.class);

        final Collection<Account> accounts = accountService.loadAll();
        final Collection<Transaction> transactions = transactionService.loadAll();
        final GameLogger gameLogger = injector.getInstance(GameLogger.class);
        final HomeController homeController = injector.getInstance(HomeController.class);
        final BetController betController = injector.getInstance(BetController.class);
        final HistoryController historyController = injector.getInstance(HistoryController.class);
        final BlackjackController blackjackController = injector.getInstance(BlackjackController.class);
        final RegistrationController registrationController = injector.getInstance(RegistrationController.class);

        final Map<Screen, Object> controllerMap = new HashMap<Screen, Object>() {{
            put(BET, betController);
            put(GAME, blackjackController);
            put(HISTORY, historyController);
            put(HOME, homeController);
            put(REGISTRATION, registrationController);
        }};

        final Map<Screen, ScreenObserver> layoutListenerMap = new HashMap<Screen, ScreenObserver>() {{
            put(BET, betController);
            put(GAME, blackjackController);
            put(HISTORY, historyController);
            put(HOME, homeController);
            put(REGISTRATION, registrationController);
        }};

        loadFXML(controllerMap);

        homeController.onAccountsLoaded(accounts);
        gameLogger.onAccountsLoaded(accounts);
        gameLogger.onTransactionsLoaded(transactions);

        screenSupervisor.initializeListeners(layoutListenerMap);
        screenSupervisor.initializeLayout(resourceMap);
    }

    public static void main(String[] args) {
        currentThread().setName("Main Thread");

        ImageMap.load();

        launch(args);
    }
}
