package main.domain.model;

import static java.util.Objects.hash;

public class AnonymousCard extends Card {

    private AnonymousCard(Suit suit, Rank rank) {
        super(rank, suit, (true));
    }

    public static AnonymousCard anonymousCard(Suit suit, Rank rank) {
        return new AnonymousCard(suit, rank);
    }

    public String shortName() {
        return suit.name().toLowerCase() + getRank().ORDINAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank == card.getRank() && suit == card.getSuit();
    }

    @Override
    public int hashCode() {
        return hash(rank, suit);
    }
}
