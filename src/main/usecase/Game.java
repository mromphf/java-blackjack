package main.usecase;

import main.domain.*;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.*;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Action.*;
import static main.usecase.Layout.HOME;
import static main.usecase.eventing.Predicate.*;

public class Game extends EventConnection implements ActionListener, BetListener, LayoutListener {

    private final UUID key;
    private final Stack<Card> deck;
    private final int maxCards;
    private final int numDecks;
    private final Map<Action, Runnable> runnableMap;
    private Round round;

    public Game(UUID key, Stack<Card> deck, int numDecks) {
        this.key = key;
        this.deck = deck;
        this.maxCards = deck.size();
        this.numDecks = numDecks;
        runnableMap = new HashMap<>();
        round = new Round(Account.placeholder().getKey(), new Stack<>(), 0, maxCards, numDecks);

        runnableMap.put(HIT, () -> round.hit());
        runnableMap.put(SPLIT, () -> round.split());
        runnableMap.put(STAND, () -> round.stand());
        runnableMap.put(SETTLE, () -> round.rewind());
        runnableMap.put(DOUBLE, () -> round.doubleDown());
        runnableMap.put(PLAY_NEXT_HAND, () -> round.playNextHand());
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onActionEvent(Event<Action> event) {
        if (event.is(ACTION_TAKEN)) {
            round.record(LocalDateTime.now(), event.getData());
            runnableMap.getOrDefault(event.getData(), () -> {}).run();
            eventNetwork.onGameUpdate(round.getSnapshot(LocalDateTime.now()));
        }
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        if (event.is(LAYOUT_CHANGED) && event.getData() == HOME) {
            round = new Round(Account.placeholder().getKey(), deck, 0, maxCards, numDecks);
        }
    }


    @Override
    public void onBetEvent(Event<Bet> event) {
        if (event.is(BET_PLACED)) {
            final Bet bet = event.getData();
            round = new Round(bet.getAccountKey(), deck, bet.getVal(), maxCards, numDecks);
            eventNetwork.onGameUpdate(round.getSnapshot(LocalDateTime.now()));
        }
    }
}
