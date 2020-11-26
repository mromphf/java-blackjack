package main.io.blackjack;

import main.AppRoot;
import main.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;
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

    private final AppRoot appRoot;
    private final Stack<Card> deck;
    private final BlackjackView blackjackView;
    private Map<String, List<Card>> hands;

    public BlackjackController(AppRoot appRoot, FXMLLoader fxmlLoader, Stack<Card> deck) throws IOException {
        fxmlLoader.setController(this);
        fxmlLoader.load();
        this.appRoot = appRoot;
        this.blackjackView = new BlackjackView(foreground);
        this.deck = deck;
        reset();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnDouble.setOnAction(event -> onDouble());
        btnNext.setOnAction(event -> moveOntoNextHand());
    }

    public void reset() {
        this.hands = openingHand(deck);
        allButtons().forEach(button -> button.setDisable(false));
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        blackjackView.reset();
        blackjackView.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        blackjackView.drawCards(ImageMap.ofConcealed(hands.get("dealer"), hands.get("player")));
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

        blackjackView.reset();
        blackjackView.drawLabels(concealedScore(hands.get("dealer")), score(hands.get("player")) );
        blackjackView.drawCards(ImageMap.ofConcealed(hands.get("dealer"), hands.get("player")));

        if (bust(hands.get("player"))) {
            revealAllHands();
            blackjackView.bust();
            onRoundOver();
        }
    }

    private void onRoundOver() {
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void moveOntoNextHand() {
        appRoot.switchToBetScreen();
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
        blackjackView.reset();
        blackjackView.drawLabels(score(hands.get("dealer")), score(hands.get("player")) );
        blackjackView.drawCards(ImageMap.of(hands.get("dealer"), hands.get("player")));
    }

    private List<Button> allButtons() {
        return new ArrayList<Button>() {{
            add(btnHit);
            add(btnDouble);
            add(btnStand);
        }};
    }
}
