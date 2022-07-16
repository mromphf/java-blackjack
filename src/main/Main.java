package main;

import com.google.inject.Injector;
import com.google.inject.Module;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import main.adapter.injection.BaseInjectionModule;
import main.adapter.log.TableLogger;
import main.adapter.storage.FileSystem;
import main.adapter.ui.ImageService;
import main.adapter.ui.HomeController;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.AccountService;
import main.adapter.ui.Screen;
import main.adapter.ui.ScreenSupervisor;
import main.usecase.TransactionService;

import java.util.Collection;
import java.util.Map;

import static com.google.inject.Guice.createInjector;
import static java.lang.Thread.currentThread;

public class Main extends Application {

    public static void main(String[] args) {
        currentThread().setName("Main Thread");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final Module baseInjectionModule = new BaseInjectionModule();
        final Injector injector = createInjector(baseInjectionModule);

        final TransactionService transactionService = injector.getInstance(TransactionService.class);
        final AccountService accountService = injector.getInstance(AccountService.class);
        final ScreenSupervisor screenSupervisor = injector.getInstance(ScreenSupervisor.class);
        final FileSystem fileSystem = injector.getInstance(FileSystem.class);
        final TableLogger gameLogger = injector.getInstance(TableLogger.class);
        final HomeController homeController = injector.getInstance(HomeController.class);
        final ImageService imageService = injector.getInstance(ImageService.class);
        final Collection<Account> accounts = accountService.loadAll();
        final Collection<Transaction> transactions = transactionService.loadAll();

        System.out.println("INFO: Loading image files...");
        imageService.loadCardImages();
        imageService.loadMiscImages();

        final Map<Screen, Parent> nodeMap = fileSystem.loadFXMLDocuments();

        homeController.onAccountsLoaded(accounts);
        gameLogger.onAccountsLoaded(accounts);
        gameLogger.onTransactionsLoaded(transactions);

        screenSupervisor.initializeLayout(nodeMap);
    }
}
