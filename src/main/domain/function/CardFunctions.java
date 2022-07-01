package main.domain.function;

import main.domain.model.Action;
import main.domain.model.Card;
import main.domain.model.Snapshot;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import static main.domain.predicate.RoundPredicate.determineOutcome;
import static main.domain.model.Action.BUY_INSURANCE;
import static main.domain.model.Action.DOUBLE;

public class CardFunctions {

    public final static int MAXIMUM_SCORE = 21;
    public final static int ACE_HIGH_SCORE = 11;

    public static Boolean atLeastOneAce(Collection<Card> hand) {
        return hand.stream().anyMatch(Card::isAce);
    }

    public static Boolean isPush(Collection<Card> handA, Collection<Card> handB) {
        return (score(handA) == score(handB)) && !isBust(handA) && !isBust(handB);
    }

    public final static IsBlackjack isBlackjack = new IsBlackjack();

    public final static Predicate<Collection<Card>> canSplit = cards -> {
        if (cards.size() == 2) {
            final int blackjackValue = cards.stream().findFirst().get().getBlackjackValue();
            return cards.stream().allMatch(card -> card.getBlackjackValue() == blackjackValue);
        } else {
            return false;
        }
    };

    public static Boolean isBust(Collection<Card> cards)  {
        return score(cards) > MAXIMUM_SCORE;
    }

    public static int score(Collection<Card> cards) {
        if (isBlackjack.test(cards)) {
            return MAXIMUM_SCORE;
        } else if (atLeastOneAce(cards) && hardTotalIsFavourable(cards)) {
            return hardTotal(cards);
        } else {
            return softTotal(cards);
        }
    }

    public static int concealedScore(Collection<Card> cards) {
        final Iterator<Card> cardIterator = cards.iterator();
        if (cardIterator.hasNext()) {
            final Card card = cardIterator.next();
            return card.isAce() ? ACE_HIGH_SCORE : card.getBlackjackValue();
        } else {
            return 0;
        }
    }

    public static int hardTotal(Collection<Card> cards) {
        return atLeastOneAce(cards)
                ? softTotal(cards) + 10
                : softTotal(cards);
    }

    public static Boolean hardTotalIsFavourable(Collection<Card> cards) {
        return hardTotal(cards) <= MAXIMUM_SCORE;
    }

    public static int softTotal(Collection<Card> cards) {
        return cards.stream().mapToInt(Card::getBlackjackValue).sum();
    }

    public static boolean playerWins(Collection<Card> playerCards, Collection<Card> dealerCards) {
        return (!isPush(playerCards, dealerCards) &&
                isBust(dealerCards) && !isBust(playerCards) ||
                (!isBust(playerCards) && score(playerCards) > score(dealerCards)));
    }

    public static int settleBet(Snapshot snapshot) {
        final Collection<Card> playerHand = snapshot.getPlayerHand();
        final Collection<Action> actionsTaken = snapshot.getActionsTaken();
        final int bet = snapshot.getBet();

        final int insurancePayout = (actionsTaken.stream()
                .anyMatch(a -> a.equals(BUY_INSURANCE)) && isBlackjack.test(snapshot.getDealerHand()))
                ? (bet * 2) : 0;

        final int betMultiplier = actionsTaken.stream().anyMatch(a -> a.equals(DOUBLE)) ? 2 : 1;

        final float blackjackMultiplier = isBlackjack.test(playerHand) ? 1.5f : 1.0f;

        switch (determineOutcome(snapshot)) {
            case BLACKJACK:
            case WIN:
                return (((int) (bet * blackjackMultiplier) * 2) * betMultiplier) + insurancePayout;
            case PUSH:
                return (bet * betMultiplier) + insurancePayout;
            default:
                return 0;
        }
    }

    public static class IsBlackjack implements Predicate<Collection<Card>> {
        @Override
        public boolean test(Collection<Card> cards) {
            boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
            boolean tenOrHigher = cards.stream().filter(c -> c.getBlackjackValue() > 9).count() == 1;
            boolean twoCards = cards.size() == 2;
            return twoCards && oneAce && tenOrHigher;
        }
    }
}
