package blackjack.ui;

import blackjack.domain.Card;
import blackjack.domain.Deck;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;

import static blackjack.domain.Deck.fresh;
import static blackjack.domain.Deck.shuffle;
import static blackjack.ui.IMAGE_KEY.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stack<Card> deck = shuffle(fresh());
        Screen screen = new Screen(foreground);

        Map<String, Set<Card>> hands = Deck.openingHand(deck);
        Iterator<Card> dealer = hands.get("dealer").iterator();
        Iterator<Card> player = hands.get("player").iterator();

        Map<IMAGE_KEY, Image> imageMap = imageMap( dealer, player );
        screen.drawCards(imageMap);
    }

    private Map<IMAGE_KEY, Image> imageMap(Iterator<Card> dealer, Iterator<Card> player) {
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
