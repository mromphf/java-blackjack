package main.io.injection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import main.domain.Card;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.io.log.ConsoleLogHandler;
import main.io.log.FileLogHandler;
import main.io.storage.*;
import main.usecase.SelectionMemory;
import main.usecase.TransactionCache;
import main.usecase.Transactor;
import main.usecase.eventing.EventNetwork;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.inject.name.Names.named;
import static java.lang.Integer.parseInt;
import static java.util.UUID.randomUUID;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.domain.Evaluate.transactionEvaluators;

public class BaseInjectionModule extends AbstractModule {

    final Map<Directory, File> directoryFileMap;

    public BaseInjectionModule(Map<Directory, File> directoryFileMap) {
        this.directoryFileMap = directoryFileMap;
    }

    @Override
    public void configure() {
        final FileSystem fileSystem = new FileSystem(directoryFileMap);
        final Properties config = fileSystem.loadConfig();
        final String deckName = (String) config.get("game.deckName");
        final int numDecks = parseInt((String) config.get("game.numDecks"));
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = transactionEvaluators();

        bind(TransactionCache.class)
                .toInstance(new TransactionCache(randomUUID(), new TreeMap<>()));

        bind(Logger.class)
                .annotatedWith(named("logger"))
                .toInstance(Logger.getLogger("Game Logger"));

        bind(AccountMemory.class).to(Database.class);
        bind(TransactionMemory.class).to(Database.class);
        bind(SelectionMemory.class).toInstance(new SelectionMemory());

        bind(Integer.class)
                .annotatedWith(named("numDecks"))
                .toInstance(numDecks);

        bind(String.class)
                .annotatedWith(named("logger"))
                .toInstance("Game Logger");

        bind(Transactor.class)
                .toInstance(new Transactor(randomUUID(), evaluators));

        bind(EventNetwork.class)
                .toInstance(new EventNetwork(randomUUID()));

        bind(new TypeLiteral<Stack<Card>>() {
        })
                .annotatedWith(named("deck"))
                .toInstance(deckName.equals("default") ? freshlyShuffledDeck(numDecks) : fileSystem.loadDeck(deckName));

        bind(new TypeLiteral<Collection<Handler>>() {})
                .annotatedWith(named("logHandlers"))
                .toInstance(new ArrayList<Handler>() {
                    {
                        add(new ConsoleLogHandler());
                        add(new FileLogHandler());
                    }
                });
    }
}
