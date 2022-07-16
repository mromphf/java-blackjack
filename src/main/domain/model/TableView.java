package main.domain.model;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.SortedMap;
import java.util.Stack;
import java.util.UUID;

import static java.lang.Math.negateExact;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSortedMap;
import static main.domain.function.CardFunctions.concealedScore;
import static main.domain.function.CardFunctions.score;
import static main.domain.function.OutcomeAssessment.settleBet;
import static main.domain.model.Outcome.UNRESOLVED;
import static main.domain.predicate.HighOrderPredicate.determineOutcome;
import static main.util.StringUtil.actionString;
import static main.util.StringUtil.playerString;

public class TableView {
    private final LocalDateTime timestamp;
    private final int bet;
    private final Outcome outcome;
    private final Collection<Card> deck;
    private final Collection<Card> dealerHand;
    private final Collection<Card> playerHand;
    private final Collection<Card> cardsToPlay;
    private final Collection<Hand> handsToSettle;
    private final SortedMap<LocalDateTime, Action> actionsTaken;
    private final Account account;

    public TableView(LocalDateTime timestamp,
                     Account account,
                     int bet,
                     Stack<Card> deck,
                     Hand dealerHand,
                     Hand playerHand,
                     Stack<Card> cardsToPlay,
                     Collection<Hand> handsToSettle,
                     SortedMap<LocalDateTime, Action> actionsTaken) {
        this.timestamp = timestamp;
        this.account = account;
        this.bet = bet;
        this.deck = unmodifiableCollection(deck);
        this.dealerHand = unmodifiableCollection(dealerHand);
        this.playerHand = unmodifiableCollection(playerHand);
        this.cardsToPlay = unmodifiableCollection(cardsToPlay);
        this.handsToSettle = unmodifiableCollection(handsToSettle);
        this.actionsTaken = unmodifiableSortedMap(actionsTaken);
        this.outcome = determineOutcome(this);
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public String balanceText() {
        return format("Balance $%s", playerBalance());
    }

    public int bet() {
        return bet;
    }

    public int negativeBet() {
        return negateExact(bet);
    }

    public int playerBalance() {
        return account.getBalance() + settleBet(this);
    }

    public Outcome outcome() {
        return outcome;
    }

    public boolean canAffordToSpendMore() {
        return playerBalance() >= bet;
    }

    public Collection<Card> dealerHand() {
        return dealerHand;
    }

    public Collection<Card> playerHand() {
        return playerHand;
    }

    public Collection<Card> cardsToPlay() {
        return cardsToPlay;
    }

    public Collection<Hand> handsToSettle() {
        return handsToSettle;
    }

    public Collection<Action> actionsTaken() {
        return actionsTaken.values();
    }

    public UUID playerAccountKey() {
        return account.getKey();
    }

    public int deckSize() {
        return deck.size();
    }

    public double deckProgress(float maxDeckSize) {
        return deck.size() / maxDeckSize;
    }

    public int dealerScore() {
        if (outcome == UNRESOLVED) {
            return concealedScore(dealerHand);
        } else {
            return score(dealerHand);
        }
    }

    public int playerScore() {
        return score(playerHand);
    }

    @Override
    public String toString() {
        return format("\n\tAccount Key: %s\n\t" +
                        "Balance: $%s,\n\t" +
                        "Cards in Deck: %s,\n\t" +
                        "Outcome: %s,\n\t" +
                        "Bet: $%s,\n\t" +
                        "Hands to Play: %s,\n\t" +
                        "Hands to Settle: %s,\n\t" +
                        "ActionsTaken: {%s\n\t},\n\t" +
                        "Player: %s,\n\t" +
                        "Dealer: %s",
                playerAccountKey(),
                playerBalance(),
                deckSize(),
                outcome,
                bet,
                cardsToPlay.size(),
                handsToSettle.size(),
                actionString(actionsTaken),
                playerString(playerHand),
                playerString(dealerHand));
    }
}
