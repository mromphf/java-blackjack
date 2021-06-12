package main.usecase;

import main.domain.*;
import main.io.EventConnection;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Action.*;
import static main.usecase.Layout.HOME;
import static main.usecase.Predicate.ACTION_TAKEN;
import static main.usecase.Predicate.BET_PLACED;

public class Game extends EventConnection implements NavListener, EventListener {

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
        round = new Round(Account.placeholder().getKey(), new Stack<>(), 0, maxCards, numDecks);

        runnableMap.put(HIT, () -> round.hit());
        runnableMap.put(SPLIT, () -> round.split());
        runnableMap.put(STAND, () -> round.stand());
        runnableMap.put(SETTLE, () -> round.rewind());
        runnableMap.put(DOUBLE, () -> round.doubleDown());
        runnableMap.put(PLAY_NEXT_HAND, () -> round.playNextHand());
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(BET_PLACED)) {
            round = new Round(message.getAccount().getKey(), deck, message.getAmount(), maxCards, numDecks);
            eventNetwork.onUpdate(round.getSnapshot(LocalDateTime.now()));
        } else if (message.is(ACTION_TAKEN)) {
            round.record(LocalDateTime.now(), message.getAction());
            runnableMap.getOrDefault(message.getAction(), () -> {}).run();
            eventNetwork.onUpdate(round.getSnapshot(LocalDateTime.now()));
        }
    }

    @Override
    public void onChangeLayout(Layout layout) {
        if (layout == HOME) {
            round = new Round(Account.placeholder().getKey(), deck, 0, maxCards, numDecks);
        }
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        onChangeLayout(layout);
    }
}
