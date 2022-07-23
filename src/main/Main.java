package main;

import com.google.inject.Injector;
import com.google.inject.Module;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import main.adapter.injection.InjectionModule;
import main.adapter.log.GameLogger;
import main.adapter.storage.FileSystem;
import main.adapter.ui.ImageStore;
import main.adapter.ui.HomeController;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.AccountStore;
import main.adapter.ui.Screen;
import main.adapter.ui.ScreenSupervisor;
import main.usecase.TransactionStore;

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
        final Module injectionModule = new InjectionModule();
        final Injector injector = createInjector(injectionModule);

        final TransactionStore transactionStore = injector.getInstance(TransactionStore.class);
        final AccountStore accountStore = injector.getInstance(AccountStore.class);
        final ScreenSupervisor screenSupervisor = injector.getInstance(ScreenSupervisor.class);
        final FileSystem fileSystem = injector.getInstance(FileSystem.class);
        final GameLogger gameLogger = injector.getInstance(GameLogger.class);
        final HomeController homeController = injector.getInstance(HomeController.class);
        final ImageStore imageStore = injector.getInstance(ImageStore.class);
        final Collection<Account> accounts = accountStore.loadAll();
        final Collection<Transaction> transactions = transactionStore.loadAll();

        System.out.println("INFO: Loading image files...");
        imageStore.loadCardImages();
        imageStore.loadMiscImages();

        final Map<Screen, Parent> nodeMap = fileSystem.loadFXMLDocuments();

        homeController.onAccountsLoaded(accounts);
        gameLogger.onAccountsLoaded(accounts);
        gameLogger.onTransactionsLoaded(transactions);

        screenSupervisor.initialize(nodeMap);
    }
}
