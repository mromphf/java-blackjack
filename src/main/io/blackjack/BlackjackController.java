package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import main.domain.StateSnapshot;
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
        btnNext.setOnAction(event -> controlListeners.forEach(ControlListener::onMoveToBettingTable));
    }

    @Override
    public void onUpdate(StateSnapshot stateSnapshot) {
        setGameButtonsDisabled(false);
        renderConcealedTable(stateSnapshot);
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        btnDouble.setDisable(stateSnapshot.atLeastOneCardDrawn);
        lblBalance.setText(String.format("Balance: $%s", stateSnapshot.balance));
    }

    @Override
    public void onDealerWins(StateSnapshot stateSnapshot) {
        turnOffControls(stateSnapshot);
        tableDisplay.drawResults("Lose", Color.RED);
    }

    @Override
    public void onPlayerWins(StateSnapshot stateSnapshot) {
        turnOffControls(stateSnapshot);
        tableDisplay.drawResults("Win", Color.GREEN);
    }

    @Override
    public void onBust(StateSnapshot stateSnapshot) {
        turnOffControls(stateSnapshot);
        tableDisplay.drawResults("Bust", Color.RED);
    }

    @Override
    public void onPush(StateSnapshot stateSnapshot) {
        turnOffControls(stateSnapshot);
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

    private void turnOffControls(StateSnapshot stateSnapshot) {
        setGameButtonsDisabled(true);
        renderExposedTable(stateSnapshot);
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void renderExposedTable(StateSnapshot stateSnapshot) {
        lblBet.setText(String.format("Bet: $%s", stateSnapshot.bet));
        lblCards.setText(String.format("Cards Remaining: %s", stateSnapshot.deckSize));
        tableDisplay.reset();
        tableDisplay.drawScores(score(stateSnapshot.dealerHand), score(stateSnapshot.playerHand));
        tableDisplay.drawCards(ImageMap.of(stateSnapshot.dealerHand, stateSnapshot.playerHand));
    }

    private void renderConcealedTable(StateSnapshot stateSnapshot) {
        lblBet.setText(String.format("Bet: $%s", stateSnapshot.bet));
        lblCards.setText(String.format("Cards Remaining: %s", stateSnapshot.deckSize));
        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(stateSnapshot.dealerHand), score(stateSnapshot.playerHand));
        tableDisplay.drawCards(ImageMap.ofConcealed(stateSnapshot.dealerHand, stateSnapshot.playerHand));
    }

    private void setGameButtonsDisabled(boolean disabled) {
        btnHit.setDisable(disabled);
        btnDouble.setDisable(disabled);
        btnStand.setDisable(disabled);
    }
}
