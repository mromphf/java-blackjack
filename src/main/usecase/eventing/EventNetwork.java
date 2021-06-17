package main.usecase.eventing;

import main.domain.Account;
import main.domain.Action;
import main.domain.Bet;
import main.domain.Snapshot;
import main.io.EventConnection;
import main.usecase.Layout;

import java.util.*;

public class EventNetwork implements
        BalanceListener,
        GameStateListener,
        Responder,
        EventListener,
        BetListener,
        LayoutListener,
        ActionListener,
        AccountListener {

    private final Collection<AccountListener> accountListeners = new ArrayList<>();
    private final Collection<BetListener> betListeners = new ArrayList<>();
    private final Collection<LayoutListener> layoutListeners = new ArrayList<>();
    private final Collection<ActionListener> actionListeners = new ArrayList<>();
    private final Collection<BalanceListener> balanceListeners = new LinkedList<>();
    private final Collection<GameStateListener> gameStateListeners = new LinkedList<>();
    private final Collection<main.usecase.eventing.EventListener> eventListeners = new LinkedList<>();
    private final Map<Predicate, Responder> responders = new HashMap<>();

    public EventNetwork(Collection<EventConnection> connections) {
        for (EventConnection connection : connections) {
            if (connection instanceof BalanceListener) {
                balanceListeners.add((BalanceListener) connection);
            }

            if (connection instanceof GameStateListener) {
                gameStateListeners.add((GameStateListener) connection);
            }

            if (connection instanceof EventListener) {
                eventListeners.add((EventListener) connection);
            }

            if (connection instanceof BetListener) {
                betListeners.add((BetListener) connection);
            }

            if (connection instanceof LayoutListener) {
                layoutListeners.add((LayoutListener) connection);
            }

            if (connection instanceof ActionListener) {
                actionListeners.add((ActionListener) connection);
            }

            if (connection instanceof AccountListener) {
                accountListeners.add((AccountListener) connection);
            }
        }
    }

    public void registerResponder(Predicate elm, Responder responder) {
        responders.put(elm, responder);
    }

    public void registerGameStateListener(GameStateListener listener) {
        gameStateListeners.add(listener);
    }

    public void registerBalanceListener(BalanceListener listener) {
        balanceListeners.add(listener);
    }

    public void registerEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    @Override
    public void onEvent(Message message) {
        eventListeners.forEach(l -> l.onEvent(message));
    }

    @Override
    public void onBalanceUpdated() {
        balanceListeners.forEach(BalanceListener::onBalanceUpdated);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        gameStateListeners.forEach(l -> l.onUpdate(snapshot));
    }

    @Override
    public Account fulfill(Predicate elm) {
        return responders.get(elm).fulfill(elm);
    }

    @Override
    public void onBetEvent(Event<Bet> event) {
        betListeners.forEach(listener -> listener.onBetEvent(event));
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        layoutListeners.forEach(listener -> listener.onLayoutEvent(event));
    }

    @Override
    public void onActionEvent(Event<Action> event) {
        actionListeners.forEach(listener -> listener.onActionEvent(event));
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        accountListeners.forEach(listener -> listener.onAccountEvent(event));
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        accountListeners.forEach(listener -> listener.onAccountsEvent(event));
    }
}
