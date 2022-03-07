package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import main.io.blackjack.ImageMap;
import main.io.injection.BaseInjectionModule;
import main.io.storage.Directory;
import main.usecase.Layout;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static main.io.storage.Directory.*;
import static main.usecase.Layout.*;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new AppRoot(stage, directoryMap(), resourceMap());
    }

    public static void main(String[] args) {
        currentThread().setName("Main Thread");
        ImageMap.load();
        launch(args);
    }

    private static Map<Directory, File> directoryMap() {
        return new HashMap<Directory, File>() {{
            put(ACCOUNTS, new File("./app-data/accounts-grouped/accounts-grouped.csv"));
            put(ACCOUNTS_CLOSED, new File("./app-data/accounts-closed/account-closures-bundled.csv"));
            put(DECKS, new File("./app-data/decks/"));
            put(LOG, new File("./app-data/log/"));
            put(TRANSACTIONS, new File("./app-data/transactions-grouped/"));
        }};
    }

    private static Map<Layout, FXMLLoader> resourceMap() {
        return new HashMap<Layout, FXMLLoader>() {{
            put(BET, new FXMLLoader(BaseInjectionModule.class.getResource("../bet/BetView.fxml")));
            put(GAME, new FXMLLoader(BaseInjectionModule.class.getResource("../blackjack/BlackjackView.fxml")));
            put(HISTORY, new FXMLLoader(BaseInjectionModule.class.getResource("../history/HistoryView.fxml")));
            put(HOME, new FXMLLoader(BaseInjectionModule.class.getResource("../home/HomeView.fxml")));
            put(REGISTRATION, new FXMLLoader(BaseInjectionModule.class.getResource("../registration/RegistrationView.fxml")));
        }};
    }
}
