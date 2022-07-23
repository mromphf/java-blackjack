package main.adapter.ui;

import javafx.scene.image.Image;
import main.Main;
import main.util.InfiniteStack;
import main.adapter.graphics.animation.Moving;
import main.adapter.graphics.Symbol;
import main.domain.model.AnonymousCard;
import main.domain.model.Card;
import main.domain.model.Suit;

import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static main.adapter.graphics.Symbol.*;
import static main.adapter.graphics.Vector.vector;
import static main.adapter.ui.Direction.LEFT;
import static main.adapter.ui.Direction.RIGHT;
import static main.domain.function.DealerFunctions.anonymousDeck;
import static main.domain.model.Suit.*;

public class ImageService {

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
        for (int i = 0; i < Symbol.values().length; i++) {
            final Symbol symbol = Symbol.values()[i];

            symbolImages.put(symbol, new Image(requireNonNull(
                    Main.class.getResource(format("/png/%s.png", symbol.VALUE))).toString()
            ));
        }
    }

    public List<Image> fromCards(Collection<Card> cards, boolean outcomeResolved) {
        return cards.stream()
                .map(card -> determineImage(card, outcomeResolved))
                .collect(toList());
    }

    public Image blankCard() {
        if (useBlueDeck) {
            return symbolImages.get(BLUE_CARD);
        } else {
            return symbolImages.get(RED_CARD);
        }
    }

    public Collection<Moving<Image>> reelRight() {
        return symbolReel(RIGHT);
    }

    public Collection<Moving<Image>> reelLeft() {
        return symbolReel(LEFT);
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

    public Image symbolImage(Suit suit) {
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

    private Image determineImage(Card card, boolean outcomeResolved) {
        if (outcomeResolved) {
            return cardImages.get(card.anonymize());
        } else {
            return card.isFaceUp() ? cardImages.get(card.anonymize()) : blankCard();
        }
    }
}
