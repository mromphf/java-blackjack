package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import main.domain.Card;
import main.usecase.Round;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    @FXML
    private Button btnNext;

    private final Round round;

    public BlackjackController(Round round) {
        this.round = round;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDouble.setOnAction(event -> onDouble());
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnNext.setOnAction(event -> onMoveToNextHand());
        reset();
    }

    public void reset() {
        List<Card> dealerHand = round.getHand("dealer");
        List<Card> playerHand = round.getHand("player");
        setGameButtonsDisabled(false);
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(dealerHand), score(playerHand));
        tableDisplay.drawCards(ImageMap.ofConcealed(dealerHand, playerHand));
    }

    public void onDouble() {
        onHit();
        dealerTurn();
    }

    public void onStand() {
        dealerTurn();
    }

    public void onHit() {
        List<Card> playerHand = round.getHand("player");
        List<Card> dealerHand = round.getHand("dealer");

        btnDouble.setDisable(true);

        round.hit();

        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(dealerHand), score(playerHand) );
        tableDisplay.drawCards(ImageMap.ofConcealed(dealerHand, playerHand));

        if (bust(playerHand)) {
            revealAllHands();
            onRoundOver();
        }
    }

    private void onMoveToNextHand() {
        round.placeBet();
        reset();
    }

    private void onRoundOver() {
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void dealerTurn() {
        round.dealerTurn();
        revealAllHands();
        onRoundOver();
    }

    private void revealAllHands() {
        List<Card> dealerHand = round.getHand("dealer");
        List<Card> playerHand = round.getHand("player");

        setGameButtonsDisabled(true);
        tableDisplay.reset();
        tableDisplay.drawScores(score(dealerHand), score(playerHand) );
        tableDisplay.drawCards(ImageMap.of(dealerHand, playerHand));

        if (round.playerBusted()) {
            tableDisplay.drawResults("Bust", Color.RED);
        } else if (push(playerHand, dealerHand)) {
            tableDisplay.drawResults("Push", Color.ORANGE);
        } else if (playerWins(playerHand, dealerHand)) {
            tableDisplay.drawResults("Win", Color.GREEN);
        } else {
            tableDisplay.drawResults("Lose", Color.RED);
        }
    }

    private void setGameButtonsDisabled(boolean disabled) {
        btnHit.setDisable(disabled);
        btnDouble.setDisable(disabled);
        btnStand.setDisable(disabled);
    }
}
