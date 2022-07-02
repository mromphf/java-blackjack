package main.adapter.graphics;

import main.domain.model.Card;
import main.domain.model.Rank;
import main.domain.model.Suit;

import java.util.Objects;

import static main.adapter.graphics.Symbol.CARD;
import static main.domain.model.Rank.ACE;
import static main.domain.model.Suit.HEARTS;

public class ImageKey {

    private final Symbol symbol;
    private final Rank rank;
    private final Suit suit;

    private ImageKey(Rank rank, Suit suit, Symbol symbol) {
        this.rank = rank;
        this.suit = suit;
        this.symbol = symbol;
    }

    public static ImageKey keyFromCard(Card card) {
        return new ImageKey(card.getRank(), card.getSuit(), CARD);
    }

    public static ImageKey keyFromSymbol(Symbol symbol) {
        return new ImageKey(ACE, HEARTS, symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageKey imageKey = (ImageKey) o;
        return symbol == imageKey.symbol && rank == imageKey.rank && suit == imageKey.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, rank, suit);
    }
}
