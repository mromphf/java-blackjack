package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import main.adapter.log.GameLogger;
import main.adapter.storage.AccountStorage;
import main.usecase.AccountCache;
import main.usecase.LayoutManager;
import main.usecase.TransactionCache;
import main.usecase.eventing.AlertListener;
import main.usecase.eventing.EventNetwork;
import main.usecase.eventing.TransactionListener;

import java.util.Collection;
import java.util.LinkedList;

import static com.google.inject.name.Names.named;

public class EventingInjectionModule extends AbstractModule {

    private final Injector baseInjector;
    private final Injector fxmlInjector;

    public EventingInjectionModule(Injector baseInjector, Injector fxmlInjector) {
        this.baseInjector = baseInjector;
        this.fxmlInjector = fxmlInjector;
    }

    @Override
    public void configure() {
        bind(EventNetwork.class).in(Singleton.class);

        bind(new TypeLiteral<Collection<AlertListener>>() {})
                .annotatedWith(named("alertListeners"))
                .toInstance(new LinkedList<AlertListener>() {{
                    add(fxmlInjector.getInstance(LayoutManager.class));
                }});

        bind(new TypeLiteral<Collection<TransactionListener>>() {})
                .annotatedWith(named("transactionListeners"))
                .toInstance(new LinkedList<TransactionListener>() {{
                    add(baseInjector.getInstance(AccountCache.class));
                    add(baseInjector.getInstance(AccountStorage.class));
                    add(baseInjector.getInstance(GameLogger.class));
                    add(baseInjector.getInstance(TransactionCache.class));
                }});
    }
}
