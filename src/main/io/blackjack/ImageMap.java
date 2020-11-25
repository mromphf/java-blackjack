package main.io.blackjack;

import javafx.scene.image.Image;
import main.domain.Card;

import java.util.*;
import java.util.stream.Collectors;

import static main.io.blackjack.IMAGE_KEY.DEALER_CARDS;
import static main.io.blackjack.IMAGE_KEY.PLAYER_CARDS;

public class ImageMap {

    public static Map<IMAGE_KEY, List<Image>> of(List<Card> dealer, List<Card> player) {
        return new HashMap<IMAGE_KEY, List<Image>>() {{
            put(DEALER_CARDS, dealer.stream().map(ImageMap::imageFileName).collect(Collectors.toList()));
            put(PLAYER_CARDS, player.stream().map(ImageMap::imageFileName).collect(Collectors.toList()));
        }};
    }

    public static Map<IMAGE_KEY, List<Image>> ofConcealed(List<Card> dealer, List<Card> player) {
        return new HashMap<IMAGE_KEY, List<Image>>() {{
            put(DEALER_CARDS, conceal(dealer));
            put(PLAYER_CARDS, player.stream().map(ImageMap::imageFileName).collect(Collectors.toList()));
        }};
    }

    private static List<Image> conceal(List<Card> cards) {
        // TODO: Need a check for empty list
        boolean useBlueDeck = new Random().nextInt(10) % 2 == 0;
        return new LinkedList<Image>() {{
            add(imageFileName(cards.get(0)));
            add(blankCard(useBlueDeck));
        }};
    }

    private static Image imageFileName(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return new Image(String.format("file:graphics/%s.jpg", imageName));
    }

    private static Image blankCard(boolean blue) {
        if (blue) {
            return new Image("file:graphics/card_blue.jpg");
        } else {
            return new Image("file:graphics/card_red.jpg");
        }
    }
}
