package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import main.adapter.log.GameLogger;
import main.adapter.storage.AccountStorage;
import main.adapter.ui.bet.BetController;
import main.adapter.ui.blackjack.BlackjackController;
import main.adapter.ui.home.HomeController;
import main.usecase.AccountCache;
import main.usecase.LayoutManager;
import main.usecase.TransactionCache;
import main.usecase.Transactor;
import main.usecase.eventing.*;

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

        bind(new TypeLiteral<Collection<AccountListener>>() {})
                .annotatedWith(named("accountListeners"))
                .toInstance(new LinkedList<AccountListener>() {{
                    add(baseInjector.getInstance(AccountCache.class));
                    add(baseInjector.getInstance(AccountStorage.class));
                    add(baseInjector.getInstance(GameLogger.class));
                    add(fxmlInjector.getInstance(HomeController.class));
                    add(baseInjector.getInstance(Transactor.class));
                }});

        bind(new TypeLiteral<Collection<SnapshotListener>>() {})
                .annotatedWith(named("snapshotListeners"))
                .toInstance(new LinkedList<SnapshotListener>() {{
                    add(baseInjector.getInstance(GameLogger.class));
                    add(baseInjector.getInstance(Transactor.class));
                    add(fxmlInjector.getInstance(BetController.class));
                    add(fxmlInjector.getInstance(BlackjackController.class));
                }});
    }
}
