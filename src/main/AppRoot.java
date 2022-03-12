package main;

import com.google.inject.Injector;
import main.adapter.log.GameLogger;
import main.adapter.storage.AccountStorage;
import main.adapter.ui.bet.BetController;
import main.adapter.ui.blackjack.BlackjackController;
import main.adapter.ui.history.HistoryController;
import main.adapter.ui.home.HomeController;
import main.adapter.ui.registration.RegistrationController;
import main.usecase.*;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.EventNetwork;

import java.util.Collection;
import java.util.LinkedList;

public class AppRoot {

    private final Injector baseInjector;
    private final Injector fxmlInjector;
    private final Injector eventingInjection;

    public AppRoot(Injector baseInjector, Injector fxmlInjector, Injector eventingInjection) {
        this.baseInjector = baseInjector;
        this.fxmlInjector = fxmlInjector;
        this.eventingInjection = eventingInjection;
    }


    public void init() {

        final Game game = baseInjector.getInstance(Game.class);
        final AccountStorage accountStorage = baseInjector.getInstance(AccountStorage.class);
        final GameLogger gameLogger = baseInjector.getInstance(GameLogger.class);
        final AccountCache accountCache = baseInjector.getInstance(AccountCache.class);
        final TransactionCache transactionCache = baseInjector.getInstance(TransactionCache.class);
        final Transactor transactor = baseInjector.getInstance(Transactor.class);

        final LayoutManager layoutManager = fxmlInjector.getInstance(LayoutManager.class);
        final HomeController homeController = fxmlInjector.getInstance(HomeController.class);
        final HistoryController historyController = fxmlInjector.getInstance(HistoryController.class);
        final BlackjackController blackjackController = fxmlInjector.getInstance(BlackjackController.class);
        final BetController betController = fxmlInjector.getInstance(BetController.class);
        final RegistrationController registrationController = fxmlInjector.getInstance(RegistrationController.class);

        final EventNetwork eventNetwork = eventingInjection.getInstance(EventNetwork.class);

        final Collection<EventConnection> eventConnections = new LinkedList<EventConnection>() {{
            add(accountCache);
            add(homeController);
            add(historyController);
            add(blackjackController);
            add(betController);
            add(registrationController);
            add(layoutManager);
            add(accountStorage);
            add(transactor);
            add(transactionCache);
            add(game);
            add(gameLogger);
        }};

        eventNetwork.registerListeners(eventConnections);
        eventConnections.forEach(lst ->lst.connectTo(eventNetwork));

        layoutManager.initializeLayout();

        new Thread(accountStorage::loadAllAccounts, "Account Load Thread").start();
        new Thread(accountStorage::loadAllTransactions, "Transaction Load Thread").start();
    }
}
