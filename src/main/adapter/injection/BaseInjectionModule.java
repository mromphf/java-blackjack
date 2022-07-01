package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import javafx.stage.Stage;
import main.adapter.log.ConsoleLogHandler;
import main.adapter.log.FileLogHandler;
import main.adapter.log.GameLogger;
import main.adapter.storage.AccountRepository;
import main.adapter.storage.Database;
import main.adapter.storage.FileSystem;
import main.adapter.storage.TransactionRepository;
import main.adapter.ui.bet.BetController;
import main.adapter.ui.blackjack.BlackjackController;
import main.adapter.ui.history.HistoryController;
import main.adapter.ui.home.HomeController;
import main.adapter.ui.registration.RegistrationController;
import main.domain.model.Deck;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;
import main.usecase.*;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.inject.name.Names.named;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.*;
import static main.adapter.injection.Bindings.*;
import static main.domain.function.Dealer.freshlyShuffledDeck;
import static main.domain.function.Evaluate.transactionEvaluators;

public class BaseInjectionModule extends AbstractModule {

    private final Deck deck;
    private final int numDecks;

    public BaseInjectionModule(FileSystem fileSystem) {
        final Properties config = fileSystem.loadConfig();
        final String deckName = (String) config.get("game.deckName");

        this.numDecks = parseInt((String) config.get("game.numDecks"));
        this.deck = deckName.equals("default") ? freshlyShuffledDeck() : fileSystem.loadDeck(deckName);
    }

    @Override
    public void configure() {

        bind(Integer.class)
                .annotatedWith(named(NUM_DECKS))
                .toInstance(numDecks);

        bind(Integer.class)
                .annotatedWith(named(MAX_CARDS))
                .toInstance(deck.size());

        bind(new TypeLiteral<Deck>() {
        })
                .annotatedWith(named(DECK))
                .toInstance(deck);

        bind(new TypeLiteral<Map<UUID, Collection<Transaction>>>() {
        })
                .annotatedWith(named(TRANSACTION_MAP))
                .toInstance(new HashMap<>());

        bind(new TypeLiteral<Stack<UUID>>() {
        })
                .annotatedWith(named(ACCOUNT_STACK))
                .toInstance(new Stack<>());

        bind(Logger.class)
                .annotatedWith(named(GAME_LOGGER))
                .toInstance(Logger.getLogger("Game Logger"));

        bind(new TypeLiteral<Collection<Handler>>() {
        })
                .annotatedWith(named(LOG_HANDLERS))
                .toInstance(new ArrayList<Handler>() {{
                    add(new ConsoleLogHandler());
                    add(new FileLogHandler());
                }});

        bind(new TypeLiteral<Collection<SnapshotListener>>() {
        })
                .annotatedWith(named(SNAPSHOT_LISTENERS))
                .toInstance(new ArrayList<>());

        bind(new TypeLiteral<Collection<Function<Snapshot, Optional<Transaction>>>>() {
        })
                .annotatedWith(named(EVALUATORS))
                .toInstance(transactionEvaluators());

        bind(AccountRepository.class).to(Database.class);
        bind(TransactionRepository.class).to(Database.class);
        bind(SelectionService.class).to(AccountService.class);

        bind(AccountService.class).in(Singleton.class);
        bind(BetController.class).in(Singleton.class);
        bind(BlackjackController.class).in(Singleton.class);
        bind(Game.class).in(Singleton.class);
        bind(GameLogger.class).in(Singleton.class);
        bind(HistoryController.class).in(Singleton.class);
        bind(HomeController.class).in(Singleton.class);
        bind(LayoutManager.class).in(Singleton.class);
        bind(RegistrationController.class).in(Singleton.class);
        bind(Stage.class).in(Singleton.class);
        bind(TransactionService.class).in(Singleton.class);
    }

    @Provides
    public Collection<SnapshotListener> snapshotListeners(AccountService accountService,
                                                          TransactionService transactionService,
                                                          GameLogger gameLogger) {
        return of(accountService, transactionService, gameLogger).collect(toSet());
    }
}
