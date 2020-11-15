package blackjack.ui;

import blackjack.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;

import static blackjack.domain.Deck.*;
import static blackjack.ui.IMAGE_KEY.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stack<Card> deck = shuffle(fresh());
        Set<Card> dealerHand = new HashSet<>();
        Set<Card> playerHand = new HashSet<>();
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());

        Iterator<Card> player = playerHand.iterator();
        Iterator<Card> dealer = dealerHand.iterator();

        Map<IMAGE_KEY, Image> imageMap = new HashMap<IMAGE_KEY, Image>() {{
            put(DLR_CARD_1, imageFile(dealer.next()));
            put(DLR_CARD_2, imageFile(dealer.next()));
            put(PLR_CARD_1, imageFile(player.next()));
            put(PLR_CARD_2, imageFile(player.next()));
        }};

        Screen screen = new Screen(foreground);
        screen.drawCards(imageMap);
    }

    private Image imageFile(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return new Image(String.format("file:graphics/%s.jpg", imageName));
    }
}
