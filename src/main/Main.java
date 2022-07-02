package main;

import com.google.inject.Injector;
import com.google.inject.Module;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import main.adapter.injection.BaseInjectionModule;
import main.adapter.log.GameLogger;
import main.adapter.storage.FileSystem;
import main.adapter.ui.blackjack.ImageMap;
import main.adapter.ui.home.HomeController;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.AccountService;
import main.usecase.Screen;
import main.usecase.ScreenSupervisor;
import main.usecase.TransactionService;

import java.util.Collection;
import java.util.Map;

import static com.google.inject.Guice.createInjector;
import static java.lang.Thread.currentThread;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        final Module baseInjectionModule = new BaseInjectionModule();
        final Injector injector = createInjector(baseInjectionModule);

        final TransactionService transactionService = injector.getInstance(TransactionService.class);
        final AccountService accountService = injector.getInstance(AccountService.class);
        final ScreenSupervisor screenSupervisor = injector.getInstance(ScreenSupervisor.class);
        final FileSystem fileSystem = injector.getInstance(FileSystem.class);
        final GameLogger gameLogger = injector.getInstance(GameLogger.class);
        final HomeController homeController = injector.getInstance(HomeController.class);

        final Collection<Account> accounts = accountService.loadAll();
        final Collection<Transaction> transactions = transactionService.loadAll();

        ImageMap.load();

        final Map<Screen, Parent> nodeMap = fileSystem.loadFXML();


        homeController.onAccountsLoaded(accounts);
        gameLogger.onAccountsLoaded(accounts);
        gameLogger.onTransactionsLoaded(transactions);

        screenSupervisor.initializeLayout(nodeMap);
    }

    public static void main(String[] args) {
        currentThread().setName("Main Thread");
        launch(args);
    }
}
