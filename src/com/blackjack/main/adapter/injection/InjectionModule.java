package com.blackjack.main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import javafx.stage.Stage;
import com.blackjack.main.adapter.graphics.ImageStore;
import com.blackjack.main.adapter.log.ConsoleLogHandler;
import com.blackjack.main.adapter.log.FileLogHandler;
import com.blackjack.main.adapter.log.GameLogger;
import com.blackjack.main.adapter.storage.*;
import com.blackjack.main.adapter.ui.*;
import com.blackjack.main.domain.function.Assessment;
import com.blackjack.main.domain.model.Deck;
import com.blackjack.main.domain.model.Transaction;
import com.blackjack.main.usecase.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.inject.name.Names.bindProperties;
import static com.google.inject.name.Names.named;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static com.blackjack.main.adapter.injection.Bindings.*;
import static com.blackjack.main.adapter.storage.QueryKey.*;
import static com.blackjack.main.adapter.ui.Screen.*;
import static com.blackjack.main.domain.function.BetAssessment.betAssessment;
import static com.blackjack.main.domain.function.DealerFunctions.shuffledFreshDeck;
import static com.blackjack.main.domain.function.DoubleDownAssessment.doubleDownAssessment;
import static com.blackjack.main.domain.function.InsuranceAssessment.insuranceAssessment;
import static com.blackjack.main.domain.function.OutcomeAssessment.outcomeAssessment;
import static com.blackjack.main.domain.function.SplitAssessment.splitAssessment;

public class InjectionModule extends AbstractModule {

    // No good for a jar.
    // TODO: Replace with a java resource
    private static final String CONFIG_PATH = "src/com/blackjack/main/resources/config/dev.properties";

    @Override
    public void configure() {
        final Deck deck = shuffledFreshDeck();

        try (final FileInputStream filoIo = new FileInputStream(CONFIG_PATH)) {
            final Properties props = new Properties();
            props.load(filoIo);
            bindProperties(binder(), props);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bind(Double.class)
                .annotatedWith(named(MAX_CARDS))
                .toInstance(deck.size() * 1.0);

        bind(new TypeLiteral<Deck>() {
        }).annotatedWith(named(DECK)).toInstance(deck);

        bind(new TypeLiteral<Map<UUID, Collection<Transaction>>>() {
        })
                .annotatedWith(named(TRANSACTION_MAP))
                .toInstance(new HashMap<>());

        bind(new TypeLiteral<Map<QueryKey, String>>() {
        })
                .annotatedWith(named(QUERIES_SQLITE))
                .toInstance(new HashMap<QueryKey, String>() {{
                    put(ALL_ACCOUNTS, SqliteQuery.SELECT_ALL_ACCOUNTS.query());
                    put(ALL_TRANSACTIONS, SqliteQuery.SELECT_ALL_TRANSACTIONS.query());
                    put(CREATE_NEW_ACCOUNT, SqliteQuery.INSERT_NEW_ACCOUNT.query());
                    put(CREATE_NEW_DECK, SqliteQuery.INSERT_NEW_DECK.query());
                    put(CREATE_NEW_ROUND, SqliteQuery.INSERT_NEW_ROUND.query());
                    put(CREATE_NEW_TRANSACTION, SqliteQuery.INSERT_NEW_TRANSACTION.query());
                    put(DELETE_ACCOUNT, SqliteQuery.CLOSE_ACCOUNT.query());
                    put(SAVE_ACTION, SqliteQuery.INSERT_NEW_ACTION.query());
                    put(SAVE_CARD_DRAWN, SqliteQuery.INSERT_CARD_DRAWN.query());
                    put(URL, SqliteQuery.CONNECTION_URL.query());
                }});

        bind(new TypeLiteral<Map<QueryKey, String>>() {
        })
                .annotatedWith(named(QUERIES_PSQL))
                .toInstance(new HashMap<QueryKey, String>() {{
                    put(ALL_ACCOUNTS, PsqlQuery.SELECT_ALL_ACCOUNTS.query());
                    put(ALL_TRANSACTIONS, PsqlQuery.SELECT_ALL_TRANSACTIONS.query());
                    put(CREATE_NEW_ACCOUNT, PsqlQuery.INSERT_NEW_ACCOUNT.query());
                    put(CREATE_NEW_DECK, "");
                    put(CREATE_NEW_ROUND, "");
                    put(CREATE_NEW_TRANSACTION, PsqlQuery.INSERT_NEW_TRANSACTION.query());
                    put(DELETE_ACCOUNT, PsqlQuery.CLOSE_ACCOUNT.query());
                    put(SAVE_ACTION, "");
                    put(SAVE_CARD_DRAWN, "");
                    put(URL, PsqlQuery.CONNECTION_URL.query());
                }});

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

        bind(AccountRepository.class).to(Database.class);
        bind(TransactionRepository.class).to(Database.class).in(Singleton.class);
        bind(StateRepository.class).to(Database.class).in(Singleton.class);
        bind(SelectionService.class).to(AccountStore.class);
        bind(ScreenManagement.class).to(ScreenSupervisor.class);
        bind(AlertService.class).to(ScreenSupervisor.class);
        bind(Game.class).to(Coordinator.class);
        bind(ImageService.class).to(ImageStore.class).in(Singleton.class);

        bind(ImageStore.class).in(Singleton.class);
        bind(AccountStore.class).in(Singleton.class);
        bind(BetController.class).in(Singleton.class);
        bind(BlackjackController.class).in(Singleton.class);
        bind(Coordinator.class).in(Singleton.class);
        bind(GameLogger.class).in(Singleton.class);
        bind(HistoryController.class).in(Singleton.class);
        bind(HomeController.class).in(Singleton.class);
        bind(ScreenSupervisor.class).in(Singleton.class);
        bind(RegistrationController.class).in(Singleton.class);
        bind(Stage.class).in(Singleton.class);
        bind(TransactionStore.class).in(Singleton.class);
    }

    @Provides
    public Collection<TableObserver> snapshotListeners(
            AccountStore accountStore,
            TransactionStore transactionStore,
            GameLogger gameLogger,
            StateStore stateStore) {
        return of(accountStore, transactionStore, gameLogger, stateStore).collect(toSet());
    }

    @Provides
    public Collection<AccountRegistrar> accountRegistrars(
            AccountStore accountStore,
            TransactionStore transactionStore,
            GameLogger gameLogger) {
        return of(accountStore, transactionStore, gameLogger).collect(toSet());
    }

    @Provides
    public Map<Screen, ScreenObserver> screenObservers(
            HomeController homeController,
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

    @Provides
    Collection<Assessment> assessors() {
        final Collection<Assessment> evaluators = new HashSet<>();

        evaluators.add(doubleDownAssessment());
        evaluators.add(insuranceAssessment());
        evaluators.add(outcomeAssessment());
        evaluators.add(betAssessment());
        evaluators.add(splitAssessment());

        return evaluators;
    }
}
