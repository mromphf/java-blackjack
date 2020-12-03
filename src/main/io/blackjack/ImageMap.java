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

    public static void load() {
        for(Card c : fresh()) {
            final String imageName = c.getSuit().name().toLowerCase() + c.getFaceValue();
            final String imagePath = String.format("/%s.jpg", imageName);
            final Image image = new Image(ImageMap.class.getResource(imagePath).toString());
            imageMap.put(imageName, image);
        }

        final String blueCardName = "card_blue";
        final String redCardName = "card_red";
        final Image blueCardImage = new Image(ImageMap.class.getResource("/card_blue.jpg").toString());
        final Image redCardImage = new Image(ImageMap.class.getResource("/card_red.jpg").toString());
        imageMap.put(blueCardName, blueCardImage);
        imageMap.put(redCardName, redCardImage);
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

    public static Image blankCard() {
        if (ImageMap.useBlueDeck) {
            return imageMap.get("card_blue");
        } else {
            return imageMap.get("card_red");
        }
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
