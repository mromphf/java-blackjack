package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.*;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;
import main.usecase.eventing.TransactionListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import static java.lang.Math.*;
import static java.time.LocalDateTime.now;
import static main.domain.Action.*;
import static main.domain.Deck.freshlyShuffledDeck;
import static main.usecase.Layout.HOME;

public class Game extends EventConnection implements LayoutListener, TransactionListener {

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

    public void onActionTaken(Action action) {
        final Optional<Account> selectedAccount = accountCache.getCurrentlySelectedAccount();
        final LocalDateTime timestamp = now();

        if (roundStack.size() > 0 && selectedAccount.isPresent()) {
            final Round currentRound = roundStack.peek();

            runnableMap.put(HIT, currentRound::hit);
            runnableMap.put(SPLIT, currentRound::split);
            runnableMap.put(STAND, currentRound::stand);
            runnableMap.put(SETTLE, currentRound::rewind);
            runnableMap.put(DOUBLE, currentRound::doubleDown);
            runnableMap.put(PLAY_NEXT_HAND, currentRound::playNextHand);

            currentRound.record(timestamp, action);
            runnableMap.getOrDefault(action, () -> {}).run();
            eventNetwork.onGameUpdate(currentRound.getSnapshot(timestamp, selectedAccount.get()));
        }
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        final Optional<Account> selectedAccount = accountCache.getCurrentlySelectedAccount();

        if (transaction.getDescription().equals("BET") && selectedAccount.isPresent()) {
            roundStack.add(new Round(deck, abs(transaction.getAmount()), maxCards, numDecks));
            eventNetwork.onGameUpdate(roundStack.peek().getSnapshot(now(), selectedAccount.get()));
        }
    }

    @Override
    public void onLayoutEvent(Layout event) {
        if (event == HOME && roundStack.size() > 0) {
            deck.clear();
            deck.addAll(freshlyShuffledDeck(numDecks));
        }
    }
}
