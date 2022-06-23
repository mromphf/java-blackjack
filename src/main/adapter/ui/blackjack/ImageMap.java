package main.adapter.ui.blackjack;

import javafx.scene.image.Image;
import main.domain.Card;
import main.domain.Hand;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static main.domain.Dealer.freshDeck;
import static main.adapter.ui.blackjack.ImageKey.DEALER_CARDS;
import static main.adapter.ui.blackjack.ImageKey.PLAYER_CARDS;

public class ImageMap {

    private static final boolean useBlueDeck = new Random().nextInt(10) % 2 == 0;
    private static final Map<String, Image> imageMap = new HashMap<>();
    private static final String BLUE_CARD = "card_blue";
    private static final String RED_CARD = "card_red";
    private static final String SYMBOL_SPADES = "sym_spades";
    private static final String SYMBOL_HEARTS = "sym_hearts";
    private static final String SYMBOL_CLUBS = "sym_clubs";
    private static final String SYMBOL_DIAMONDS = "sym_diamonds";

    public static void load() {
        for(Card c : freshDeck()) {
            final String imageName = c.getSuit().name().toLowerCase() + c.getRank().VALUE;
            final String imagePath = format("/graphics/%s.png", imageName);
            final Image image = new Image(requireNonNull(ImageMap.class.getResource(imagePath)).toString());
            imageMap.put(imageName, image);
        }

        imageMap.put(BLUE_CARD, getImage(BLUE_CARD));
        imageMap.put(RED_CARD, getImage(RED_CARD));
        imageMap.put(SYMBOL_CLUBS, getImage(SYMBOL_CLUBS));
        imageMap.put(SYMBOL_DIAMONDS, getImage(SYMBOL_DIAMONDS));
        imageMap.put(SYMBOL_HEARTS, getImage(SYMBOL_HEARTS));
        imageMap.put(SYMBOL_SPADES, getImage(SYMBOL_SPADES));
    }

    public static Map<ImageKey, List<Image>> of(Collection<Card> dealer, Collection<Card> player) {
        return new HashMap<ImageKey, List<Image>>() {{
            put(DEALER_CARDS, dealer.stream().map(ImageMap::imageByCard).collect(Collectors.toList()));
            put(PLAYER_CARDS, player.stream().map(ImageMap::imageByCard).collect(Collectors.toList()));
        }};
    }

    public static Map<ImageKey, List<Image>> ofConcealed(Collection<Card> dealer, Collection<Card> player) {
        return new HashMap<ImageKey, List<Image>>() {{
            put(DEALER_CARDS, conceal(dealer));
            put(PLAYER_CARDS, player.stream().map(ImageMap::imageByCard).collect(Collectors.toList()));
        }};
    }

    public static List<List<Image>> ofHandsToSettle(Collection<Hand> handsToSettle) {
        return handsToSettle.stream()
                .map(cards -> cards.stream()
                        .map(ImageMap::imageByCard)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static Image blankCard() {
        if (ImageMap.useBlueDeck) {
            return imageMap.get(BLUE_CARD);
        } else {
            return imageMap.get(RED_CARD);
        }
    }

    public static Image symSpades() {
        return imageMap.get(SYMBOL_SPADES);
    }

    public static Image symHearts() {
        return imageMap.get(SYMBOL_HEARTS);
    }

    public static Image symClubs() {
        return imageMap.get(SYMBOL_CLUBS);
    }

    public static Image symDiamonds() {
        return imageMap.get(SYMBOL_DIAMONDS);
    }

    private static List<Image> conceal(Collection<Card> cards) {
        if (cards.isEmpty()) {
            return new LinkedList<>();
        } else {
            return new LinkedList<Image>() {{
                add(imageByCard(cards.iterator().next()));
                add(blankCard());
            }};
        }
    }

    private static Image imageByCard(Card c) {
        return imageMap.get(c.getSuit().name().toLowerCase() + c.getRank().VALUE);
    }

    private static Image getImage(String blueCard) {
        return new Image(requireNonNull(
                ImageMap.class.getResource(format("/graphics/%s.png", blueCard))).toString()
        );
    }
}
