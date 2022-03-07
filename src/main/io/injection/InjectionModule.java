package main.io.injection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.AppRoot;
import main.domain.Card;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.log.ConsoleLogHandler;
import main.io.log.FileLogHandler;
import main.io.registration.RegistrationController;
import main.io.storage.*;
import main.usecase.Layout;
import main.usecase.LayoutManager;
import main.usecase.Transactor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.inject.name.Names.named;
import static java.lang.Integer.parseInt;
import static java.util.UUID.randomUUID;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.domain.Evaluate.transactionEvaluators;
import static main.io.storage.Directory.*;
import static main.usecase.Layout.*;

public class InjectionModule extends AbstractModule {

    @Override
    public void configure() {
        final Map<Directory, File> directoryMap = new HashMap<Directory, File>() {{
            put(ACCOUNTS, new File("./app-data/accounts-grouped/accounts-grouped.csv"));
            put(ACCOUNTS_CLOSED, new File("./app-data/accounts-closed/account-closures-bundled.csv"));
            put(DECKS, new File("./app-data/decks/"));
            put(LOG, new File("./app-data/log/"));
            put(TRANSACTIONS, new File("./app-data/transactions-grouped/"));
        }};

        final Map<Layout, FXMLLoader> resourceMap = new HashMap<Layout, FXMLLoader>() {{
            put(HOME, new FXMLLoader(InjectionModule.class.getResource("../home/HomeView.fxml")));
            put(BET, new FXMLLoader(InjectionModule.class.getResource("../bet/BetView.fxml")));
            put(GAME, new FXMLLoader(InjectionModule.class.getResource("../blackjack/BlackjackView.fxml")));
            put(HISTORY, new FXMLLoader(InjectionModule.class.getResource("../history/HistoryView.fxml")));
            put(REGISTRATION, new FXMLLoader(InjectionModule.class.getResource("../registration/RegistrationView.fxml")));
        }};

        resourceMap.keySet().forEach(k -> {
            try {
                resourceMap.get(k).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final Map<Layout, Parent> layoutMap = resourceMap.keySet()
                .stream()
                .collect(Collectors.toMap(layout -> layout, layout -> resourceMap.get(layout).getRoot()));

        final FileSystem fileSystem = new FileSystem(directoryMap);
        final Properties config = fileSystem.loadConfig();
        final String deckName = (String) config.get("game.deckName");
        final int numDecks = parseInt((String) config.get("game.numDecks"));
        final Stack<Card> deck = deckName.equals("default") ? freshlyShuffledDeck(numDecks) : fileSystem.loadDeck(deckName);
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = transactionEvaluators();
        final Scene scene = new Scene(layoutMap.get(HOME));

        bind(Logger.class)
                .annotatedWith(named("logger"))
                .toInstance(Logger.getLogger("Game Logger"));

        bind(BetController.class)
                .toInstance(resourceMap.get(BET).getController());

        bind(BlackjackController.class)
                .toInstance(resourceMap.get(GAME).getController());

        bind(HistoryController.class)
                .toInstance(resourceMap.get(HISTORY).getController());

        bind(HomeController.class)
                .toInstance(resourceMap.get(HOME).getController());

        bind(RegistrationController.class)
                .toInstance(resourceMap.get(REGISTRATION).getController());


        bind(AccountMemory.class).to(Database.class);
        bind(TransactionMemory.class).to(Database.class);
        bind(Scene.class).toInstance(scene);

        bind(Integer.class)
                .annotatedWith(named("numDecks"))
                .toInstance(numDecks);

        bind(String.class)
                .annotatedWith(named("logger"))
                .toInstance("Game Logger");

        bind(Transactor.class)
                .toInstance(new Transactor(randomUUID(), evaluators));

        bind(new TypeLiteral<Stack<Card>>() {
        })
                .annotatedWith(named("deck"))
                .toInstance(deck);

        bind(new TypeLiteral<Collection<Handler>>() {})
                .annotatedWith(named("logHandlers"))
                .toInstance(new ArrayList<Handler>() {
                    {
                        add(new ConsoleLogHandler());
                        add(new FileLogHandler());
                    }
                });


        bind(LayoutManager.class)
                .toInstance(new LayoutManager(randomUUID(), AppRoot.stage, scene, layoutMap));

        bind(main.usecase.TransactionMemory.class)
                .toInstance(new main.usecase.TransactionMemory(randomUUID(), new TreeMap<>()));
    }
}
