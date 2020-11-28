package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import main.usecase.RoundListener;
import main.usecase.Round;

import java.net.URL;
import java.util.ResourceBundle;

import static main.domain.Rules.*;

public class BlackjackController implements Initializable, RoundListener {

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
    }

    @Override
    public void onBetPlaced() {
        reset();
    }

    public void reset() {
        setGameButtonsDisabled(false);
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        renderConcealedTable();
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
        round.hit();
        renderConcealedTable();

        if (round.playerBusted()) {
            showdown();
            onRoundOver();
        }
    }

    private void onMoveToNextHand() {
        round.placeBet();
    }

    private void onRoundOver() {
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void dealerTurn() {
        round.dealerTurn();
        showdown();
        onRoundOver();
    }

    private void showdown() {
        setGameButtonsDisabled(true);
        renderExposedTable();

        if (round.playerBusted()) {
            tableDisplay.drawResults("Bust", Color.RED);
        } else if (round.isPush()) {
            tableDisplay.drawResults("Push", Color.ORANGE);
        } else if (round.playerHasWon()) {
            tableDisplay.drawResults("Win", Color.GREEN);
        } else {
            tableDisplay.drawResults("Lose", Color.RED);
        }
    }

    private void renderExposedTable() {
        tableDisplay.reset();
        tableDisplay.drawBet(round.getBet());
        tableDisplay.drawScores(
                score(round.getHand("dealer")),
                score(round.getHand("player")) );
        tableDisplay.drawCards(
                ImageMap.of(
                        round.getHand("dealer"),
                        round.getHand("player")));
    }

    private void renderConcealedTable() {
        tableDisplay.reset();
        tableDisplay.drawBet(round.getBet());
        tableDisplay.drawScores(
                concealedScore(round.getHand("dealer")),
                score(round.getHand("player")));
        tableDisplay.drawCards(
                ImageMap.ofConcealed(
                        round.getHand("dealer"),
                        round.getHand("player")));
    }

    private void setGameButtonsDisabled(boolean disabled) {
        btnHit.setDisable(disabled);
        btnDouble.setDisable(disabled);
        btnStand.setDisable(disabled);
    }
}
