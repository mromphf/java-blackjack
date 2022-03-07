package main.io.injection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import main.io.log.FileLogHandler;
import main.io.storage.AccountMemory;
import main.io.storage.Database;
import main.io.storage.Directory;
import main.io.storage.TransactionMemory;
import main.usecase.AccountCache;
import main.usecase.TransactionCache;
import main.usecase.Transactor;
import main.usecase.eventing.EventNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.inject.name.Names.named;
import static java.util.UUID.randomUUID;
import static main.domain.Evaluate.transactionEvaluators;

public class BaseInjectionModule extends AbstractModule {

    final Map<Directory, File> directoryFileMap;

    public BaseInjectionModule(Map<Directory, File> directoryFileMap) {
        this.directoryFileMap = directoryFileMap;
    }

    @Override
    public void configure() {
        bind(TransactionCache.class)
                .toInstance(new TransactionCache(randomUUID(), new TreeMap<>()));

        bind(Logger.class)
                .annotatedWith(named("logger"))
                .toInstance(Logger.getLogger("Game Logger"));

        bind(AccountMemory.class).to(Database.class);
        bind(TransactionMemory.class).to(Database.class);
        bind(AccountCache.class).toInstance(new AccountCache());

        bind(Transactor.class)
                .toInstance(new Transactor(randomUUID(), transactionEvaluators()));

        bind(EventNetwork.class)
                .toInstance(new EventNetwork(randomUUID()));

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
