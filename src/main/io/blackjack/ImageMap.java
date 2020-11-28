package main.io.blackjack;

import javafx.scene.image.Image;
import main.domain.Card;

import java.util.*;
import java.util.stream.Collectors;

import static main.io.blackjack.ImageKey.DEALER_CARDS;
import static main.io.blackjack.ImageKey.PLAYER_CARDS;

public class ImageMap {

    private static final boolean useBlueDeck = new Random().nextInt(10) % 2 == 0;

    public static Map<ImageKey, List<Image>> of(Collection<Card> dealer, Collection<Card> player) {
        return new HashMap<ImageKey, List<Image>>() {{
            put(DEALER_CARDS, dealer.stream().map(ImageMap::imageFileName).collect(Collectors.toList()));
            put(PLAYER_CARDS, player.stream().map(ImageMap::imageFileName).collect(Collectors.toList()));
        }};
    }

    public static Map<ImageKey, List<Image>> ofConcealed(Collection<Card> dealer, Collection<Card> player) {
        return new HashMap<ImageKey, List<Image>>() {{
            put(DEALER_CARDS, conceal(dealer));
            put(PLAYER_CARDS, player.stream().map(ImageMap::imageFileName).collect(Collectors.toList()));
        }};
    }

    public static Image blankCard() {
        if (ImageMap.useBlueDeck) {
            return new Image("file:graphics/card_blue.jpg");
        } else {
            return new Image("file:graphics/card_red.jpg");
        }
    }

    private static List<Image> conceal(Collection<Card> cards) {
        if (cards.isEmpty()) {
            return new LinkedList<>();
        } else {
            return new LinkedList<Image>() {{
                add(imageFileName(cards.iterator().next()));
                add(blankCard());
            }};
        }
    }

    private static Image imageFileName(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return new Image(String.format("file:graphics/%s.jpg", imageName));
    }
}
