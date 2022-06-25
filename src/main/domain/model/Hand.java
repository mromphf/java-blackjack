package main.domain.model;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;

public class Hand extends HashSet<Card> {

    private Hand() {
        super();
    }

    private Hand(Collection<Card> cards) {
        super(cards);
    }

    public static Hand emptyHand() {
        return new Hand();
    }

    public static Hand handOf(Card... cards) {
        return new Hand(asList(cards));
    }
}
