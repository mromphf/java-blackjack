package main.io.injection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
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
import main.io.storage.AccountMemory;
import main.io.storage.Database;
import main.io.storage.FileSystem;
import main.io.storage.TransactionMemory;
import main.usecase.Layout;
import main.usecase.LayoutManager;
import main.usecase.Transactor;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.inject.name.Names.named;
import static java.lang.Integer.parseInt;
import static java.util.UUID.randomUUID;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.domain.Evaluate.transactionEvaluators;
import static main.io.ResourceLoader.*;
import static main.usecase.Layout.*;

public class InjectionModule extends AbstractModule {

    @Override
    public void configure() {
        final Map<Layout, Parent> layoutMap = loadLayoutMap();

        final FileSystem fileSystem = new FileSystem(getDirectoryMap());
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
                .toInstance((BetController) loadController(BET));

        bind(BlackjackController.class)
                .toInstance((BlackjackController) loadController(GAME));

        bind(HistoryController.class)
                .toInstance((HistoryController) loadController(HISTORY));

        bind(HomeController.class)
                .toInstance((HomeController) loadController(HOME));

        bind(RegistrationController.class)
                .toInstance((RegistrationController) loadController(REGISTRATION));


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
