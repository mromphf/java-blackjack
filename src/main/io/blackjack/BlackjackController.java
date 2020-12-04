package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import main.domain.Snapshot;
import main.io.RootController;
import main.usecase.ControlListener;
import main.usecase.OutcomeListener;
import main.usecase.GameStateListener;

import java.net.URL;
import java.util.ResourceBundle;

import static main.domain.Rules.*;

public class BlackjackController extends RootController implements Initializable, GameStateListener, OutcomeListener {

    @FXML
    private Label lblBet;

    @FXML
    private Label lblBalance;

    @FXML
    private Label lblCards;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDouble.setOnAction(event -> onDouble());
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnNext.setOnAction(event -> controlListeners.forEach(ControlListener::onStartNewRound));
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        setGameButtonsDisabled(false);
        renderConcealedTable(snapshot);
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        btnDouble.setDisable(snapshot.atLeastOneCardDrawn);
        lblBalance.setText(String.format("Balance: $%s", snapshot.balance));
    }

    @Override
    public void onDealerWins(Snapshot snapshot) {
        turnOffControls(snapshot);
        tableDisplay.drawResults("Lose", Color.RED);
    }

    @Override
    public void onPlayerWins(Snapshot snapshot) {
        turnOffControls(snapshot);
        tableDisplay.drawResults("Win", Color.GREEN);
    }

    @Override
    public void onBust(Snapshot snapshot) {
        turnOffControls(snapshot);
        tableDisplay.drawResults("Bust", Color.RED);
    }

    @Override
    public void onPush(Snapshot snapshot) {
        turnOffControls(snapshot);
        tableDisplay.drawResults("Push", Color.ORANGE);
    }

    private void onDouble() {
        controlListeners.forEach(ControlListener::onDouble);
    }

    private void onStand() {
        controlListeners.forEach(ControlListener::onDealerTurn);
    }

    private void onHit() {
        controlListeners.forEach(ControlListener::onHit);
    }

    private void turnOffControls(Snapshot snapshot) {
        if (snapshot.isRoundFinished) {
            btnNext.setOnAction(event -> controlListeners.forEach(ControlListener::onMoveToBettingTable));
        }
        setGameButtonsDisabled(true);
        renderExposedTable(snapshot);
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void renderExposedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.bet));
        lblCards.setText(String.format("Cards Remaining: %s", snapshot.deckSize));
        tableDisplay.reset();
        tableDisplay.drawScores(score(snapshot.dealerHand), score(snapshot.playerHand));
        tableDisplay.drawCards(ImageMap.of(snapshot.dealerHand, snapshot.playerHand));
    }

    private void renderConcealedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.bet));
        lblCards.setText(String.format("Cards Remaining: %s", snapshot.deckSize));
        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(snapshot.dealerHand), score(snapshot.playerHand));
        tableDisplay.drawCards(ImageMap.ofConcealed(snapshot.dealerHand, snapshot.playerHand));
    }

    private void setGameButtonsDisabled(boolean disabled) {
        btnHit.setDisable(disabled);
        btnDouble.setDisable(disabled);
        btnStand.setDisable(disabled);
    }
}
