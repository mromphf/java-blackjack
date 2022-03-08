package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import main.adapter.log.FileLogHandler;
import main.adapter.storage.AccountMemory;
import main.adapter.storage.Database;
import main.adapter.storage.FileSystem;
import main.adapter.storage.TransactionMemory;
import main.domain.Card;
import main.usecase.AccountCache;
import main.usecase.Game;
import main.usecase.TransactionCache;
import main.usecase.Transactor;
import main.usecase.eventing.EventNetwork;

import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.inject.name.Names.named;
import static java.lang.Integer.parseInt;
import static java.util.UUID.randomUUID;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.domain.Evaluate.transactionEvaluators;

public class BaseInjectionModule extends AbstractModule {

    private final FileSystem fileSystem;

    public BaseInjectionModule(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void configure() {
        final Properties config = fileSystem.loadConfig();
        final String deckName = (String) config.get("game.deckName");
        final int numDecks = parseInt((String) config.get("game.numDecks"));

        bind(Game.class).asEagerSingleton();

        bind(Integer.class)
                .annotatedWith(named("numDecks"))
                .toInstance(numDecks);

        bind(new TypeLiteral<Stack<Card>>() {})
                .annotatedWith(named("deck"))
                .toInstance(deckName.equals("default") ? freshlyShuffledDeck(numDecks) : fileSystem.loadDeck(deckName));

        bind(TransactionCache.class)
                .toInstance(new TransactionCache(randomUUID(), new TreeMap<>()));

        bind(Logger.class)
                .annotatedWith(named("logger"))
                .toInstance(Logger.getLogger("Game Logger"));

        bind(AccountMemory.class).to(Database.class);
        bind(TransactionMemory.class).to(Database.class);
        bind(AccountCache.class).toInstance(new AccountCache());

        bind(Transactor.class)
                .toInstance(new Transactor(transactionEvaluators()));

        bind(EventNetwork.class)
                .toInstance(new EventNetwork());

        bind(new TypeLiteral<Collection<Handler>>() {})
                .annotatedWith(named("logHandlers"))
                .toInstance(new ArrayList<Handler>() {
                    {
                        //add(new ConsoleLogHandler());
                        add(new FileLogHandler());
                    }
                });
    }
}
