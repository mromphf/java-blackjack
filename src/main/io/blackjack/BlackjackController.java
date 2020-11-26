package main.io.blackjack;

import main.AppRoot;
import main.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import main.Layout;

import java.net.URL;
import java.util.*;

import static main.domain.Deck.*;
import static main.domain.Rules.*;

public class BlackjackController implements Initializable {

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

    private final Stack<Card> deck;
    private GameDisplay gameDisplay;
    private Map<String, List<Card>> hands;

    public BlackjackController() {
        this.deck = shuffle(fresh());
        this.hands = openingHand(deck);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gameDisplay = new GameDisplay(foreground);
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnDouble.setOnAction(event -> onDouble());
        btnNext.setOnAction(event -> moveOntoNextHand());
        reset();
    }

    public void reset() {
        this.hands = openingHand(deck);
        allButtons().forEach(button -> button.setDisable(false));
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        gameDisplay.reset();
        gameDisplay.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        gameDisplay.drawCards(ImageMap.ofConcealed(hands.get("dealer"), hands.get("player")));
    }

    public void onDouble() {
        onHit();
        dealerTurn();
    }

    public void onStand() {
        dealerTurn();
    }

    public void onHit() {
        btnDouble.setDisable(true);
        // TODO: Need safety check for empty deck
        hands.get("player").add(deck.pop());

        gameDisplay.reset();
        gameDisplay.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        gameDisplay.drawCards(ImageMap.ofConcealed(hands.get("dealer"), hands.get("player")));

        if (bust(hands.get("player"))) {
            revealAllHands();
            gameDisplay.bust();
            onRoundOver();
        }
    }

    private void onRoundOver() {
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void moveOntoNextHand() {
        AppRoot.setLayout(Layout.BET);
        reset();
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
        gameDisplay.reset();
        gameDisplay.drawLabels(score(hands.get("dealer")), score(hands.get("player")) );
        gameDisplay.drawCards(ImageMap.of(hands.get("dealer"), hands.get("player")));
    }

    private List<Button> allButtons() {
        return new ArrayList<Button>() {{
            add(btnHit);
            add(btnDouble);
            add(btnStand);
        }};
    }
}
