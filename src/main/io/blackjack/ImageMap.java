package main.io.blackjack;

import javafx.scene.image.Image;
import main.domain.Card;

import java.util.*;
import java.util.stream.Collectors;

import static main.domain.Deck.fresh;
import static main.io.blackjack.ImageKey.DEALER_CARDS;
import static main.io.blackjack.ImageKey.PLAYER_CARDS;

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
        for(Card c : fresh()) {
            final String imageName = c.getSuit().name().toLowerCase() + c.getFaceValue();
            final String imagePath = String.format("/graphics/%s.png", imageName);
            final Image image = new Image(ImageMap.class.getResource(imagePath).toString());
            imageMap.put(imageName, image);
        }

        final Image blueCardImage = new Image(ImageMap.class.getResource(String.format("/graphics/%s.png", BLUE_CARD)).toString());
        final Image redCardImage = new Image(ImageMap.class.getResource(String.format("/graphics/%s.png", RED_CARD)).toString());
        final Image clubsImage = new Image(ImageMap.class.getResource(String.format("/graphics/%s.png", SYMBOL_CLUBS)).toString());
        final Image diamondsImage = new Image(ImageMap.class.getResource(String.format("/graphics/%s.png", SYMBOL_DIAMONDS)).toString());
        final Image heartsImage = new Image(ImageMap.class.getResource(String.format("/graphics/%s.png", SYMBOL_HEARTS)).toString());
        final Image spadesImage = new Image(ImageMap.class.getResource(String.format("/graphics/%s.png", SYMBOL_SPADES)).toString());

        imageMap.put(BLUE_CARD, blueCardImage);
        imageMap.put(RED_CARD, redCardImage);
        imageMap.put(SYMBOL_CLUBS, clubsImage);
        imageMap.put(SYMBOL_DIAMONDS, diamondsImage);
        imageMap.put(SYMBOL_HEARTS, heartsImage);
        imageMap.put(SYMBOL_SPADES, spadesImage);
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

    public static List<List<Image>> ofHandsToSettle(Stack<Stack<Card>> handsToSettle) {
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

    public static Image blankBlueCard() {
        return imageMap.get(BLUE_CARD);
    }

    public static Image blankRedCard() {
        return imageMap.get(RED_CARD);
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
        return imageMap.get(c.getSuit().name().toLowerCase() + c.getFaceValue());
    }
}
