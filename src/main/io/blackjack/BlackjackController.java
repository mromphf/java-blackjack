package main.io.blackjack;

import main.AppRoot;
import main.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import main.Layout;

import java.net.URL;
import java.util.*;

import static main.domain.Deck.*;
import static main.domain.Rules.*;

public class BlackjackController implements Initializable {

    @FXML
    private TableDisplay tableDisplay;

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

    private final Stack<Card> deck;
    private Map<String, List<Card>> hands;

    public BlackjackController() {
        this.deck = shuffle(fresh());
        this.hands = openingHand(deck);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reset();
    }

    public void reset() {
        this.hands = openingHand(deck);
        List<Card> dealerHand = hands.get("dealer");
        List<Card> playerHand = hands.get("player");
        setGameButtonsDisabled(false);
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        tableDisplay.reset();
        tableDisplay.drawLabels(concealedScore(dealerHand), score(playerHand));
        tableDisplay.drawCards(ImageMap.ofConcealed(dealerHand, playerHand));
    }

    @FXML
    public void onDouble() {
        onHit();
        dealerTurn();
    }

    @FXML
    public void onStand() {
        dealerTurn();
    }

    @FXML
    public void onHit() {
        List<Card> playerHand = hands.get("player");
        List<Card> dealerHand = hands.get("dealer");

        btnDouble.setDisable(true);
        // TODO: Need safety check for empty deck
        playerHand.add(deck.pop());

        tableDisplay.reset();
        tableDisplay.drawLabels(concealedScore(dealerHand), score(playerHand) );
        tableDisplay.drawCards(ImageMap.ofConcealed(dealerHand, playerHand));

        if (bust(playerHand)) {
            revealAllHands();
            onRoundOver();
        }
    }

    @FXML
    private void onMoveToNextHand() {
        AppRoot.setLayout(Layout.BET);
        reset();
    }

    private void onRoundOver() {
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
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
        List<Card> dealerHand = hands.get("dealer");
        List<Card> playerHand = hands.get("player");

        setGameButtonsDisabled(true);
        tableDisplay.reset();
        tableDisplay.drawLabels(score(dealerHand), score(playerHand) );
        tableDisplay.drawCards(ImageMap.of(dealerHand, playerHand));

        if (bust(playerHand)) {
            tableDisplay.bust();
        } else if (push(playerHand, dealerHand)) {
            tableDisplay.push();
        }
    }

    private void setGameButtonsDisabled(boolean disabled) {
        new ArrayList<Button>() {{
            add(btnHit);
            add(btnDouble);
            add(btnStand);
        }}.forEach(b -> b.setDisable(disabled));
    }
}
