package blackjack.io;

import blackjack.domain.Card;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static blackjack.domain.Deck.*;
import static blackjack.io.IMAGE_KEY.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @FXML
    private Button btnStand;

    private List<Card> deck;
    private Map<String, List<Card>> hands;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Screen screen = new Screen(foreground);
        btnStand.setOnAction(new StandHandler(screen));

        deck = shuffle(fresh());
        hands = openingHand(deck);
        Map<IMAGE_KEY, List<Image>> imageMap = imageMap(hands.get("dealer"), hands.get("player"));
        deck = burn(4, deck);

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

    private static class StandHandler implements EventHandler<ActionEvent> {

        private final Screen screen;

        public StandHandler(Screen screen) {
            this.screen = screen;
        }

        @Override
        public void handle(ActionEvent event) {
            screen.blackScreen();
        }
    }
}
