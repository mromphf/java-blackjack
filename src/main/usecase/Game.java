package main.usecase;

import main.domain.*;
import main.usecase.eventing.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static main.domain.Action.*;
import static main.usecase.eventing.Predicate.*;

public class Game extends EventConnection implements ActionListener, BetListener {

    private final UUID key;
    private final Stack<Card> deck;
    private final int maxCards;
    private final int numDecks;
    private final Map<Action, Runnable> runnableMap = new HashMap<>();
    private final Stack<Round> roundStack = new Stack<>();

    public Game(UUID key, Stack<Card> deck, int numDecks) {
        this.key = key;
        this.deck = deck;
        this.maxCards = deck.size();
        this.numDecks = numDecks;
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onActionEvent(Event<Action> event) {
        if (event.is(ACTION_TAKEN) && roundStack.size() > 0) {
            final Round currentRound = roundStack.peek();

            runnableMap.put(HIT, currentRound::hit);
            runnableMap.put(SPLIT, currentRound::split);
            runnableMap.put(STAND, currentRound::stand);
            runnableMap.put(SETTLE, currentRound::rewind);
            runnableMap.put(DOUBLE, currentRound::doubleDown);
            runnableMap.put(PLAY_NEXT_HAND, currentRound::playNextHand);

            currentRound.record(now(), event.getData());
            runnableMap.getOrDefault(event.getData(), () -> {}).run();
            eventNetwork.onGameUpdate(currentRound.getSnapshot(now()));
        }
    }

    @Override
    public void onBetEvent(Event<Bet> event) {
        if (event.is(BET_PLACED)) {
            final Bet bet = event.getData();

            roundStack.add(new Round(bet.getAccountKey(), deck, bet.getVal(), maxCards, numDecks));
            eventNetwork.onGameUpdate(roundStack.peek().getSnapshot(now()));
        }
    }
}
