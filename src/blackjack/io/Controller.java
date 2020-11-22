package blackjack.io;

import blackjack.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static blackjack.domain.Deck.*;
import static blackjack.domain.Rules.*;
import static blackjack.io.IMAGE_KEY.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @FXML
    private Button btnStand;

    @FXML
    private Button btnHit;

    @FXML
    private Button btnDouble;

    private Stack<Card> deck;
    private Map<String, List<Card>> hands;
    private Screen screen;
    private boolean useBlueDeck;

    public Controller(Stage stage, FXMLLoader fxmlLoader) throws IOException {
        fxmlLoader.setController(this);
        fxmlLoader.load();
        stage.setScene(new Scene(fxmlLoader.getRoot()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        useBlueDeck = new Random().nextInt(10) % 2 == 0;
        screen = new Screen(foreground);
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnDouble.setOnAction(event -> onDouble());

        deck = shuffle(fresh());
        hands = openingHand(deck);

        screen.reset();
        screen.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        screen.drawCards(concealedImageMap(hands.get("dealer"), hands.get("player")));
    }

    public void onDouble() {
        screen.blueScreen();
    }

    public void onStand() {
        dealerTurn();
    }

    public void onHit() {
        btnDouble.setDisable(true);
        // TODO: Need safety check for empty deck
        hands.get("player").add(deck.pop());

        screen.reset();
        screen.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        screen.drawCards(concealedImageMap(hands.get("dealer"), hands.get("player")));

        if (bust(hands.get("player"))) {
            revealAllHands();
            screen.bust();
        }
    }

    private void dealerTurn() {
        while (score(hands.get("dealer")) < 16) {
            // TODO: Need safety check for empty deck
            hands.get("dealer").add(deck.pop());
        }
        revealAllHands();
    }

    private void revealAllHands() {
        allButtons().forEach(b -> b.setDisable(true));
        screen.reset();
        screen.drawLabels(score(hands.get("dealer")), score(hands.get("player")) );
        screen.drawCards(imageMap(hands.get("dealer"), hands.get("player")));
    }

    private List<Button> allButtons() {
        return new ArrayList<Button>() {{
            add(btnHit);
            add(btnDouble);
            add(btnStand);
        }};
    }

    private Map<IMAGE_KEY, List<Image>> imageMap(List<Card> dealer, List<Card> player) {
        return new HashMap<IMAGE_KEY, List<Image>>() {{
            put(DEALER_CARDS, dealer.stream().map(Controller::imageFileName).collect(Collectors.toList()));
            put(PLAYER_CARDS, player.stream().map(Controller::imageFileName).collect(Collectors.toList()));
        }};
    }

    private Map<IMAGE_KEY, List<Image>> concealedImageMap(List<Card> dealer, List<Card> player) {
        return new HashMap<IMAGE_KEY, List<Image>>() {{
            put(DEALER_CARDS, conceal(dealer));
            put(PLAYER_CARDS, player.stream().map(Controller::imageFileName).collect(Collectors.toList()));
        }};
    }

    private List<Image> conceal(List<Card> cards) {
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

