package main.adapter.ui.blackjack;

import javafx.scene.image.Image;
import main.adapter.graphics.Symbol;
import main.domain.model.AnonymousCard;
import main.domain.model.Card;
import main.domain.model.Hand;
import main.domain.model.Suit;

import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static main.adapter.graphics.Symbol.*;
import static main.domain.function.Dealer.anonymousDeck;

public class ImageMap {

    private static final boolean useBlueDeck = new Random().nextInt(10) % 2 == 0;

    private static final Map<AnonymousCard, Image> cardImages = new HashMap<>();
    private static final Map<Symbol, Image> symbolImages = new HashMap<>();

    public static void loadImageMap() {
        for(AnonymousCard c : anonymousDeck()) {
            final String imageName = c.getSuit().name().toLowerCase() + c.getRank().ORDINAL;
            final String imagePath = format("/graphics/%s.png", imageName);

            final Image image = new Image(requireNonNull(ImageMap.class.getResource(imagePath)).toString());

            cardImages.put(c, image);
        }

        for (int i = 0; i < Symbol.values().length ; i++) {
            final Symbol symbol = values()[i];

            symbolImages.put(symbol, new Image(requireNonNull(
                    ImageMap.class.getResource(format("/graphics/%s.png", symbol.VALUE))).toString()
            ));
        }
    }

    public static Image[] imageArray(Collection<Card> cards, boolean outcomeResolved) {
        final Image[] arr = new Image[cards.size()];
        final Iterator<Card> iter = cards.iterator();

        int i = 0;

        while (iter.hasNext()) {
            final Card c = iter.next();

            if (outcomeResolved) {
                arr[i] = cardImages.get(c.anonymize());
            } else {
                arr[i] = c.isFaceUp() ? cardImages.get(c.anonymize()) : blankCard();
            }

            i++;
        }

        return arr;
    }

    public static List<List<Image>> ofHandsToSettle(Collection<Hand> handsToSettle) {
        return handsToSettle.stream()
                .map(cards -> cards.stream()
                        .map(c -> c.isFaceUp() ? cardImages.get(c.anonymize()) : blankCard())
                        .collect(toList()))
                .collect(toList());
    }

    public static Image blankCard() {
        if (ImageMap.useBlueDeck) {
            return symbolImages.get(BLUE_CARD);
        } else {
            return symbolImages.get(RED_CARD);
        }
    }

    public static Image symbolImage(Suit suit) {
        switch (suit) {
            case SPADES:
                return symbolImages.get(SYMBOL_SPADES);
            case CLUBS:
                return symbolImages.get(SYMBOL_CLUBS);
            case DIAMONDS:
                return symbolImages.get(SYMBOL_DIAMONDS);
            default:
                return symbolImages.get(SYMBOL_HEARTS);
        }
    }
}
