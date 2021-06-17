package main.usecase;

import main.domain.*;
import main.io.EventConnection;
import main.usecase.eventing.EventListener;
import main.usecase.eventing.Message;

import java.time.LocalDateTime;
import java.util.*;

import static main.domain.Action.*;
import static main.usecase.Layout.HOME;
import static main.usecase.eventing.Predicate.*;

public class Game extends EventConnection implements EventListener {

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
            final Bet bet = message.getBet();
            round = new Round(bet.getAccountKey(), deck, bet.getVal(), maxCards, numDecks);
            eventNetwork.onUpdate(round.getSnapshot(LocalDateTime.now()));
        } else if (message.is(ACTION_TAKEN)) {
            round.record(LocalDateTime.now(), message.getAction());
            runnableMap.getOrDefault(message.getAction(), () -> {}).run();
            eventNetwork.onUpdate(round.getSnapshot(LocalDateTime.now()));
        } else if (message.is(LAYOUT_CHANGED)) {
            onChangeLayout(message.getLayout());
        }
    }

    public void onChangeLayout(Layout layout) {
        if (layout == HOME) {
            round = new Round(Account.placeholder().getKey(), deck, 0, maxCards, numDecks);
        }
    }
}
