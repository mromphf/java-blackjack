package main.usecase;

import main.domain.*;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Action.*;
import static main.usecase.Layout.HOME;

public class Game extends EventConnection implements ActionListener, NavListener {

    private final Stack<Card> deck;
    private final int maxCards;
    private final int numDecks;
    private final Map<Action, Runnable> runnableMap;
    private Round round;

    public Game(Stack<Card> deck, int numDecks) {
        this.deck = deck;
        this.maxCards = deck.size();
        this.numDecks = numDecks;
        runnableMap = new HashMap<>();
        round = new Round(Account.placeholder().getKey(),0, new Stack<>(), maxCards, numDecks);

        runnableMap.put(HIT, () -> round.hit());
        runnableMap.put(SPLIT, () -> round.split());
        runnableMap.put(STAND, () -> round.stand());
        runnableMap.put(SETTLE, () -> round.rewind());
        runnableMap.put(DOUBLE, () -> round.doubleDown());
        runnableMap.put(PLAY_NEXT_HAND, () -> round.playNextHand());
    }

    @Override
    public void onBetPlaced(Account account, int amount) {
        round = new Round(account.getKey(), amount, deck, maxCards, numDecks);
        eventNetwork.onUpdate(round.getSnapshot());
    }

    @Override
    public void onActionTaken(Action action) {
        round.record(LocalDateTime.now(), action);
        runnableMap.getOrDefault(action, () -> {}).run();
        eventNetwork.onUpdate(round.getSnapshot());
    }

    @Override
    public void onChangeLayout(Layout layout) {
        if (layout == HOME) {
            round = new Round(Account.placeholder().getKey(), 0, deck, maxCards, numDecks);
        }
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        onChangeLayout(layout);
    }
}
