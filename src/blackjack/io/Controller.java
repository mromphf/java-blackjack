package blackjack.io;

import blackjack.domain.Card;
import blackjack.domain.Deck;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static blackjack.domain.Deck.fresh;
import static blackjack.domain.Deck.shuffle;
import static blackjack.io.IMAGE_KEY.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stack<Card> deck = shuffle(fresh());
        Screen screen = new Screen(foreground);
        Map<String, List<Card>> hands = Deck.openingHand(deck);
        Map<IMAGE_KEY, List<Image>> imageMap = imageMap(hands.get("dealer"), hands.get("player"));
        screen.drawCards(imageMap);
    }

    private Map<IMAGE_KEY, List<Image>> imageMap(List<Card> dealer, List<Card> player) {
        return new HashMap<IMAGE_KEY, List<Image>>() {{
            put(DEALER_CARDS, dealer.stream().map(Controller::imageFileName).collect(Collectors.toList()));
            put(PLAYER_CARDS, player.stream().map(Controller::imageFileName).collect(Collectors.toList()));
        }};
    }

    private static Image imageFileName(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return new Image(String.format("file:graphics/%s.jpg", imageName));
    }
}
