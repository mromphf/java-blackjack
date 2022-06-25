package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import main.adapter.log.ConsoleLogHandler;
import main.adapter.log.FileLogHandler;
import main.adapter.log.GameLogger;
import main.adapter.storage.AccountRepository;
import main.adapter.storage.Database;
import main.adapter.storage.FileSystem;
import main.adapter.storage.TransactionRepository;
import main.domain.Deck;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.AccountCache;
import main.usecase.Game;
import main.usecase.TransactionCache;
import main.usecase.Transactor;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.inject.name.Names.named;
import static java.lang.Integer.parseInt;
import static main.adapter.injection.Bindings.*;
import static main.domain.Dealer.freshlyShuffledDeck;
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
        bind(Game.class).in(Singleton.class);
        bind(AccountRepository.class).to(Database.class);
        bind(TransactionRepository.class).to(Database.class);
        bind(AccountCache.class).in(Singleton.class);
        bind(Transactor.class).in(Singleton.class);
        bind(TransactionCache.class).in(Singleton.class);
        bind(GameLogger.class).in(Singleton.class);

        bind(Integer.class)
                .annotatedWith(named(NUM_DECKS))
                .toInstance(numDecks);

        bind(Integer.class)
                .annotatedWith(named(MAX_CARDS))
                        .toInstance(deck.size());

        bind(new TypeLiteral<Deck>() {})
                .annotatedWith(named(DECK))
                .toInstance(deck);

        bind(new TypeLiteral<Map<UUID, Collection<Transaction>>>() {})
                .annotatedWith(named(TRANSACTION_MAP))
                .toInstance(new HashMap<>());

        bind(new TypeLiteral<Stack<UUID>>() {})
                .annotatedWith(named(ACCOUNT_STACK))
                .toInstance(new Stack<>());

        bind(Logger.class)
                .annotatedWith(named(GAME_LOGGER))
                .toInstance(Logger.getLogger("Game Logger"));

        bind(new TypeLiteral<Collection<Handler>>() {})
                .annotatedWith(named(LOG_HANDLERS))
                .toInstance(new ArrayList<Handler>() {{
                    add(new ConsoleLogHandler());
                    add(new FileLogHandler());
                }});

        bind(new TypeLiteral<Collection<Function<Snapshot, Optional<Transaction>>>>() {})
                .annotatedWith(named(EVALUATORS))
                .toInstance(transactionEvaluators());
    }
}
