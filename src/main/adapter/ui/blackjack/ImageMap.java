package main.adapter.ui.blackjack;

import javafx.scene.image.Image;
import main.domain.model.Card;
import main.domain.model.Hand;

import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static main.adapter.graphics.ImageKey.ofSymbol;
import static main.adapter.graphics.Symbol.*;
import static main.domain.function.Dealer.freshDeck;

public class ImageMap {

    private static final boolean useBlueDeck = new Random().nextInt(10) % 2 == 0;
    private static final Map<String, Image> imageMap = new HashMap<>();
    private static final Map<main.adapter.graphics.ImageKey, Image> imageMap2 = new HashMap<>();

    public static void loadImageMap() {
        for(Card c : freshDeck()) {
            final String imageName = c.getSuit().name().toLowerCase() + c.getRank().ORDINAL;
            final String imagePath = format("/graphics/%s.png", imageName);
            final Image image = new Image(requireNonNull(ImageMap.class.getResource(imagePath)).toString());
            imageMap.put(imageName, image);
            imageMap2.put(main.adapter.graphics.ImageKey.ofCard(c), image);
        }

        imageMap2.put(ofSymbol(BLUE_CARD), getImage(BLUE_CARD.VALUE));
        imageMap2.put(ofSymbol(RED_CARD), getImage(RED_CARD.VALUE));
        imageMap2.put(ofSymbol(SYMBOL_CLUBS), getImage(SYMBOL_CLUBS.VALUE));
        imageMap2.put(ofSymbol(SYMBOL_DIAMONDS), getImage(SYMBOL_DIAMONDS.VALUE));
        imageMap2.put(ofSymbol(SYMBOL_HEARTS), getImage(SYMBOL_HEARTS.VALUE));
        imageMap2.put(ofSymbol(SYMBOL_SPADES), getImage(SYMBOL_SPADES.VALUE));
    }

    public static Map<OLD_IMAGE_KEY, List<Image>> of(Collection<Card> dealer, Collection<Card> player) {
        return new HashMap<OLD_IMAGE_KEY, List<Image>>() {{
            put(OLD_IMAGE_KEY.DEALER_CARDS, dealer.stream().map(ImageMap::imageByCard).collect(toList()));
            put(OLD_IMAGE_KEY.PLAYER_CARDS, player.stream().map(ImageMap::imageByCard).collect(toList()));
        }};
    }

    public static Map<OLD_IMAGE_KEY, List<Image>> ofConcealed(Collection<Card> dealer, Collection<Card> player) {
        return new HashMap<OLD_IMAGE_KEY, List<Image>>() {{
            put(OLD_IMAGE_KEY.DEALER_CARDS, conceal(dealer));
            put(OLD_IMAGE_KEY.PLAYER_CARDS, player.stream().map(ImageMap::imageByCard).collect(toList()));
        }};
    }

    public static List<List<Image>> ofHandsToSettle(Collection<Hand> handsToSettle) {
        return handsToSettle.stream()
                .map(cards -> cards.stream()
                        .map(ImageMap::imageByCard)
                        .collect(toList()))
                .collect(toList());
    }

    public static Image blankCard() {
        if (ImageMap.useBlueDeck) {
            return imageMap2.get(ofSymbol(BLUE_CARD));
        } else {
            return imageMap2.get(ofSymbol(RED_CARD));
        }
    }

    public static Image symSpades() {
        return imageMap2.get(ofSymbol(SYMBOL_SPADES));
    }

    public static Image symHearts() {
        return imageMap2.get(ofSymbol(SYMBOL_HEARTS));
    }

    public static Image symClubs() {
        return imageMap2.get(ofSymbol(SYMBOL_CLUBS));
    }

    public static Image symDiamonds() {
        return imageMap2.get(ofSymbol(SYMBOL_DIAMONDS));
    }

    private static List<Image> conceal(Collection<Card> cards) {
        return cards.stream().map(card -> card.isFaceUp () ? imageByCard(card) : blankCard()).collect(toList());
    }

    private static Image imageByCard(Card c) {
        return imageMap.get(c.getSuit().name().toLowerCase() + c.getRank().ORDINAL);
    }

    private static Image getImage(String blueCard) {
        return new Image(requireNonNull(
                ImageMap.class.getResource(format("/graphics/%s.png", blueCard))).toString()
        );
    }
}
