package blackjack.io;

import blackjack.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static blackjack.domain.Deck.*;
import static blackjack.io.IMAGE_KEY.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Card> deck = shuffle(fresh());
        Map<String, List<Card>> hands = openingHand(deck);
        Map<IMAGE_KEY, List<Image>> imageMap = imageMap(hands.get("dealer"), hands.get("player"));
        deck = burn(4, deck);

        Screen screen = new Screen(foreground);
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
