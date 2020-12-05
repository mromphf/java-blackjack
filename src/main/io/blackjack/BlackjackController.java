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
import main.usecase.Outcome;
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
    private GridPane splitControls;

    @FXML
    private Button btnDouble;

    @FXML
    private Button btnNext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnNext.setOnAction(event -> controlListeners.forEach(ControlListener::onSettleHand));
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        renderConcealedTable(snapshot);
        gameControls.setVisible(!canSplit(snapshot.playerHand));
        splitControls.setVisible(canSplit(snapshot.playerHand));
        gameOverControls.setVisible(false);
        btnDouble.setDisable(snapshot.atLeastOneCardDrawn);
        lblBalance.setText(String.format("Balance: $%s", snapshot.balance));
    }

    @Override
    public void onOutcomeDecided(Snapshot snapshot, Outcome outcome) {
        if (snapshot.isRoundFinished) {
            btnNext.setOnAction(event -> controlListeners.forEach(ControlListener::onMoveToBettingTable));
        }

        renderExposedTable(snapshot);
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);

        switch (outcome) {
            case WIN:
                tableDisplay.drawResults("Win", Color.GREEN);
                break;
            case LOSE:
                tableDisplay.drawResults("Lose", Color.RED);
                break;
            case BUST:
                tableDisplay.drawResults("Bust", Color.RED);
                break;
            default:
                tableDisplay.drawResults("Push", Color.ORANGE);
                break;
        }
    }

    @FXML
    public void onSplit() {
        controlListeners.forEach(ControlListener::onSplit);
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        controlListeners.forEach(ControlListener::onStand);
    }

    @FXML
    private void onHit() {
        controlListeners.forEach(ControlListener::onHit);
    }

    @FXML
    private void onDouble() {
        controlListeners.forEach(ControlListener::onDouble);
    }

    private void renderExposedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.bet));
        lblCards.setText(String.format("Cards Remaining: %s", snapshot.deckSize));
        tableDisplay.reset();
        tableDisplay.drawScores(score(snapshot.dealerHand), score(snapshot.playerHand));
        tableDisplay.drawCards(ImageMap.of(snapshot.dealerHand, snapshot.playerHand));
        tableDisplay.drawHandsToPlay(ImageMap.ofHandsToSettle(snapshot.handsToPlay));
    }

    private void renderConcealedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.bet));
        lblCards.setText(String.format("Cards Remaining: %s", snapshot.deckSize));
        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(snapshot.dealerHand), score(snapshot.playerHand));
        tableDisplay.drawCards(ImageMap.ofConcealed(snapshot.dealerHand, snapshot.playerHand));
        tableDisplay.drawHandsToPlay(ImageMap.ofHandsToSettle(snapshot.handsToPlay));
    }
}
