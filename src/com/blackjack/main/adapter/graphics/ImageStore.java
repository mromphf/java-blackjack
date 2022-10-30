package com.blackjack.main.adapter.graphics;

import com.blackjack.main.Main;
import com.blackjack.main.adapter.ui.ImageService;
import com.blackjack.main.domain.model.AnonymousCard;
import com.blackjack.main.domain.model.Card;
import com.blackjack.main.domain.model.Suit;
import com.blackjack.main.util.InfiniteStack;
import javafx.scene.image.Image;

import java.util.*;
import java.util.stream.Stream;

import static com.blackjack.main.adapter.graphics.Direction.LEFT;
import static com.blackjack.main.adapter.graphics.Direction.RIGHT;
import static com.blackjack.main.adapter.graphics.Symbol.*;
import static com.blackjack.main.adapter.graphics.Vector.vector;
import static com.blackjack.main.domain.function.DealerFunctions.anonymousDeck;
import static com.blackjack.main.domain.model.Suit.*;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

public class ImageStore implements ImageService {

    private final static int REEL_LENGTH = 23;
    private final static int REEL_SPEED = 1;
    private final static int IMG_SIZE = 40;
    private final boolean useBlueDeck = new Random().nextInt(10) % 2 == 0;

    private final Map<AnonymousCard, Image> cardImages = new HashMap<>();
    private final Map<Symbol, Image> symbolImages = new HashMap<>();

    public void loadCardImages() {
        for (AnonymousCard c : anonymousDeck()) {
            final String imageName = c.shortName();
            final String imagePath = format("/png/%s.png", imageName);

            final Image image = new Image(requireNonNull(
                    Main.class.getResource(imagePath)).toString());

            cardImages.put(c, image);
        }
    }

    public void loadMiscImages() {
        for (Symbol symbol: Symbol.values()) {
            symbolImages.put(symbol, new Image(requireNonNull(
                    Main.class.getResource(format("/png/%s.png", symbol.VALUE))).toString()
            ));
        }
    }

    public List<Image> fromCards(Stream<Card> cards) {
        return cards.sorted()
                .map(this::determineImage)
                .collect(toList());
    }

    public Collection<Moving<Image>> reelRight() {
        return symbolReel(RIGHT);
    }

    public Collection<Moving<Image>> reelLeft() {
        return symbolReel(LEFT);
    }

    private Image determineImage(Card card) {
        return card.isFaceUp() ? cardImages.get(card.anonymize()) : blankCard();
    }

    private Image blankCard() {
        if (useBlueDeck) {
            return symbolImages.get(BLUE_CARD);
        } else {
            return symbolImages.get(RED_CARD);
        }
    }

    private Collection<Moving<Image>> symbolReel(Direction direction) {
        final Collection<Moving<Image>> reel = new LinkedList<>();
        final InfiniteStack<Image> symbols = new InfiniteStack<>(symbolImages());

        for (int i = 0, position = 0; i < REEL_LENGTH; i++, position += IMG_SIZE) {
            reel.add(new Moving<>(
                    symbols.pop(),
                    vector(position, IMG_SIZE),
                    REEL_SPEED, direction));
        }

        return reel;
    }

    private Collection<Image> symbolImages() {
        return of(
                symbolImage(HEARTS),
                symbolImage(CLUBS),
                symbolImage(DIAMONDS),
                symbolImage(SPADES)
        ).collect(toList());
    }

    private Image symbolImage(Suit suit) {
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
