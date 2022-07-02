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
import main.adapter.storage.TransactionRepository;
import main.adapter.ui.*;
import main.adapter.ui.bet.BetController;
import main.adapter.ui.blackjack.BlackjackController;
import main.adapter.ui.blackjack.ImageMap;
import main.adapter.ui.history.HistoryController;
import main.adapter.ui.home.HomeController;
import main.adapter.ui.registration.RegistrationController;
import main.domain.Assessment;
import main.domain.Game;
import main.domain.model.Deck;
import main.domain.model.Transaction;
import main.usecase.*;

import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.inject.name.Names.named;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static main.adapter.injection.Bindings.*;
import static main.domain.function.Dealer.freshlyShuffledDeck;
import static main.domain.function.Evaluate.transactionEvaluators;
import static main.adapter.ui.Screen.*;
import static main.adapter.ui.Screen.HOME;

public class BaseInjectionModule extends AbstractModule {

    private final Deck deck;

    public BaseInjectionModule() {
        this.deck = freshlyShuffledDeck();
    }

    @Override
    public void configure() {

        bind(Integer.class)
                .annotatedWith(named(NUM_DECKS))
                .toInstance((4));

        bind(Integer.class)
                .annotatedWith(named(MAX_CARDS))
                .toInstance(deck.size());

        bind(new TypeLiteral<Deck>() {
        }).annotatedWith(named(DECK)).toInstance(deck);

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

        bind(new TypeLiteral<Collection<Assessment>>() {
        }).annotatedWith(named(EVALUATORS)).toInstance(transactionEvaluators());

        bind(AccountRepository.class).to(Database.class);
        bind(TransactionRepository.class).to(Database.class);
        bind(SelectionService.class).to(AccountService.class);
        bind(ScreenManagement.class).to(ScreenSupervisor.class);
        bind(AlertService.class).to(ScreenSupervisor.class);

        bind(ImageMap.class).in(Singleton.class);
        bind(AccountService.class).in(Singleton.class);
        bind(BetController.class).in(Singleton.class);
        bind(BlackjackController.class).in(Singleton.class);
        bind(Game.class).in(Singleton.class);
        bind(GameLogger.class).in(Singleton.class);
        bind(HistoryController.class).in(Singleton.class);
        bind(HomeController.class).in(Singleton.class);
        bind(ScreenSupervisor.class).in(Singleton.class);
        bind(RegistrationController.class).in(Singleton.class);
        bind(Stage.class).in(Singleton.class);
        bind(TransactionService.class).in(Singleton.class);
    }

    @Provides
    public Collection<GameObserver> snapshotListeners(AccountService accountService,
                                                      TransactionService transactionService,
                                                      GameLogger gameLogger) {
        return of(accountService, transactionService, gameLogger).collect(toSet());
    }

    @Provides
    public Collection<AccountRegistrar> accountRegistrars(AccountService accountService,
                                                          TransactionService transactionService,
                                                          GameLogger gameLogger) {
        return of(accountService, transactionService, gameLogger).collect(toSet());
    }

    @Provides
    public Map<Screen, ScreenObserver> screenObservers(HomeController homeController,
                                                       RegistrationController registrationController,
                                                       BlackjackController blackjackController,
                                                       BetController betController,
                                                       HistoryController historyController) {
        final Map<Screen, ScreenObserver> screenMap = new HashMap<>();

        screenMap.put(HOME, homeController);
        screenMap.put(BET, betController);
        screenMap.put(REGISTRATION, registrationController);
        screenMap.put(HISTORY, historyController);
        screenMap.put(GAME, blackjackController);

        return screenMap;
    }
}
