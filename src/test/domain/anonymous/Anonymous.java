package test.domain.anonymous;

import main.domain.Card;
import main.domain.Rank;
import main.domain.Suit;

import java.util.Random;

import static main.domain.Card.card;

public class Anonymous {

    public static Suit anonSuit() {
        return Suit.values()[new Random().nextInt(Suit.values().length)] ;
    }

    public static Rank anonRank() {
        return Rank.values()[new Random().nextInt(Rank.values().length)] ;
    }

    public static Card anonCard() {
        return card(anonRank(), anonSuit());
    }
}
