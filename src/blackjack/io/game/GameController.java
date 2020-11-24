package blackjack.io.game;

import blackjack.Main;
import blackjack.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static blackjack.domain.Deck.*;
import static blackjack.domain.Rules.*;
import static blackjack.io.game.IMAGE_KEY.*;

public class GameController implements Initializable {

    @FXML
    private Canvas foreground;

    @FXML
    private GridPane gameControls;

    @FXML
    private GridPane gameOverControls;

    @FXML
    private Button btnStand;

    @FXML
    private Button btnHit;

    @FXML
    private Button btnDouble;

    @FXML
    private Button btnNext;

    private final Main main;
    private Stack<Card> deck;
    private Map<String, List<Card>> hands;
    private GameView gameView;
    private boolean useBlueDeck;

    public GameController(Main main, FXMLLoader fxmlLoader) throws IOException {
        fxmlLoader.setController(this);
        fxmlLoader.load();
        this.main = main;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        useBlueDeck = new Random().nextInt(10) % 2 == 0;
        gameView = new GameView(foreground);
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnDouble.setOnAction(event -> onDouble());
        btnNext.setOnAction(event -> moveOntoNextHand());

        deck = shuffle(fresh());
        hands = openingHand(deck);

        gameView.reset();
        gameView.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        gameView.drawCards(concealedImageMap(hands.get("dealer"), hands.get("player")));
    }

    public void onDouble() {
        gameView.blueScreen();
    }

    public void onStand() {
        dealerTurn();
    }

    public void onHit() {
        btnDouble.setDisable(true);
        // TODO: Need safety check for empty deck
        hands.get("player").add(deck.pop());

        gameView.reset();
        gameView.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        gameView.drawCards(concealedImageMap(hands.get("dealer"), hands.get("player")));

        if (bust(hands.get("player"))) {
            revealAllHands();
            gameView.bust();
            onRoundOver();
        }
    }

    private void onRoundOver() {
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void moveOntoNextHand() {
        main.switchToBetScreen();
    }

    private void dealerTurn() {
        while (score(hands.get("dealer")) < 16) {
            // TODO: Need safety check for empty deck
            hands.get("dealer").add(deck.pop());
        }
        revealAllHands();
        onRoundOver();
    }

    private void revealAllHands() {
        allButtons().forEach(b -> b.setDisable(true));
        gameView.reset();
        gameView.drawLabels(score(hands.get("dealer")), score(hands.get("player")) );
        gameView.drawCards(imageMap(hands.get("dealer"), hands.get("player")));
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
            put(DEALER_CARDS, dealer.stream().map(GameController::imageFileName).collect(Collectors.toList()));
            put(PLAYER_CARDS, player.stream().map(GameController::imageFileName).collect(Collectors.toList()));
        }};
    }

    private Map<IMAGE_KEY, List<Image>> concealedImageMap(List<Card> dealer, List<Card> player) {
        return new HashMap<IMAGE_KEY, List<Image>>() {{
            put(DEALER_CARDS, conceal(dealer));
            put(PLAYER_CARDS, player.stream().map(GameController::imageFileName).collect(Collectors.toList()));
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

