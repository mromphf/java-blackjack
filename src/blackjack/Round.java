package blackjack;

import blackjack.domain.Card;
import blackjack.ui.IMAGE_KEY;
import javafx.scene.image.Image;

import java.util.*;

import static blackjack.domain.Deck.fresh;
import static blackjack.domain.Deck.shuffle;
import static blackjack.ui.IMAGE_KEY.*;

public class Round {
    public static Map<IMAGE_KEY, Image> opening() {
        Stack<Card> deck = shuffle(fresh());

        Set<Card> dealerHand = new HashSet<>();
        Set<Card> playerHand = new HashSet<>();

        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());

        Iterator<Card> player = playerHand.iterator();
        Iterator<Card> dealer = dealerHand.iterator();

        return new HashMap<IMAGE_KEY, Image>() {{
            put(DLR_CARD_1, imageFile(dealer.next()));
            put(DLR_CARD_2, imageFile(dealer.next()));
            put(PLR_CARD_1, imageFile(player.next()));
            put(PLR_CARD_2, imageFile(player.next()));
        }};
    }

    private static Image imageFile(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return new Image(String.format("file:graphics/%s.jpg", imageName));
    }
}
