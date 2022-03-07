package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.*;
import main.usecase.eventing.*;

import java.util.*;

import static java.util.UUID.randomUUID;
import static main.domain.Action.*;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.usecase.Layout.HOME;
import static main.usecase.eventing.Predicate.*;

public class Game extends EventConnection implements ActionListener, BetListener, LayoutListener {

    private final UUID key = randomUUID();
    private final Stack<Card> deck;
    private final int maxCards;
    private final int numDecks;
    private final Map<Action, Runnable> runnableMap = new HashMap<>();
    private final Stack<Round> roundStack = new Stack<>();
    private final AccountCache accountCache;

    @Inject
    public Game(@Named("deck") Stack<Card> deck, @Named("numDecks") int numDecks, AccountCache accountCache) {
        this.accountCache = accountCache;
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
        final Optional<Account> selectedAccount = accountCache.getLastSelectedAccount();

        if (event.is(ACTION_TAKEN) && roundStack.size() > 0 && selectedAccount.isPresent()) {
            final Round currentRound = roundStack.peek();

            runnableMap.put(HIT, currentRound::hit);
            runnableMap.put(SPLIT, currentRound::split);
            runnableMap.put(STAND, currentRound::stand);
            runnableMap.put(SETTLE, currentRound::rewind);
            runnableMap.put(DOUBLE, currentRound::doubleDown);
            runnableMap.put(PLAY_NEXT_HAND, currentRound::playNextHand);

            currentRound.record(event.getTimestamp(), event.getData());
            runnableMap.getOrDefault(event.getData(), () -> {}).run();
            eventNetwork.onGameUpdate(currentRound.getSnapshot(event.getTimestamp(), selectedAccount.get()));
        }
    }

    @Override
    public void onBetEvent(Event<Bet> event) {
        final Optional<Account> selectedAccount = accountCache.getLastSelectedAccount();

        if (event.is(BET_PLACED) && selectedAccount.isPresent()) {
            final Bet bet = event.getData();

            roundStack.add(new Round(deck, bet.getVal(), maxCards, numDecks));
            eventNetwork.onGameUpdate(roundStack.peek().getSnapshot(event.getTimestamp(), selectedAccount.get()));
        }
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        if (event.is(LAYOUT_CHANGED) && event.getData() == HOME && roundStack.size() > 0) {
            deck.clear();
            deck.addAll(freshlyShuffledDeck(numDecks));
        }
    }
}
