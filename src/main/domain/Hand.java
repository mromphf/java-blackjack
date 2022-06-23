package main.domain;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class Hand extends HashSet<Card> {

    public static Set<Card> handOf(Card... cards) {
        return new HashSet<>(asList(cards));
    }
}
