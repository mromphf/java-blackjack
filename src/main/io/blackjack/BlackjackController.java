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
import main.usecase.GameStateListener;
import main.usecase.NavListener;

import java.net.URL;
import java.util.ResourceBundle;

import static main.domain.Outcome.*;
import static main.domain.Rules.*;

public class BlackjackController extends RootController implements Initializable, GameStateListener {

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
    private GridPane settleControls;

    @FXML
    private GridPane gameOverControls;

    @FXML
    private GridPane insuranceControls;

    @FXML
    private GridPane splitControls;

    @FXML
    private Button btnDouble;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onUpdate(Snapshot snapshot) {
        insuranceControls.setVisible(snapshot.isInsuranceAvailable() && snapshot.is(UNRESOLVED));
        gameControls.setVisible(!canSplit(snapshot.getPlayerHand()) && snapshot.is(UNRESOLVED) && !snapshot.isInsuranceAvailable());
        splitControls.setVisible(canSplit(snapshot.getPlayerHand()) && snapshot.is(UNRESOLVED) && !snapshot.isInsuranceAvailable());
        settleControls.setVisible(snapshot.isResolved() && !snapshot.isRoundFinished());
        gameOverControls.setVisible(snapshot.isRoundFinished() && snapshot.isResolved());

        btnDouble.setDisable(snapshot.isAtLeastOneCardDrawn());
        lblBalance.setText(String.format("Balance: $%s", snapshot.getBalance()));

        if (snapshot.isResolved()) {
            renderExposedTable(snapshot);

            switch (snapshot.getOutcome()) {
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
        } else {
            renderConcealedTable(snapshot);
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
    private void onSettleNextHand() {
        controlListeners.forEach(ControlListener::onSettleHand);
    }

    @FXML
    private void onDone() {
        navListeners.forEach(NavListener::onMoveToBettingTable);
    }

    @FXML
    private void onHit() {
        controlListeners.forEach(ControlListener::onHit);
    }

    @FXML
    private void onDouble() {
        controlListeners.forEach(ControlListener::onDouble);
    }

    @FXML
    private void onTakeInsurance() {
        controlListeners.forEach(ControlListener::onPurchaseInsurance);
    }

    @FXML
    private void onNoInsurance() {
        insuranceControls.setVisible(false);
        gameControls.setVisible(true);
    }

    private void renderExposedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.getBet()));
        lblCards.setText(String.format("Cards Remaining: %s", snapshot.getDeckSize()));
        tableDisplay.reset();
        tableDisplay.drawScores(score(snapshot.getDealerHand()), score(snapshot.getPlayerHand()));
        tableDisplay.drawCards(ImageMap.of(snapshot.getDealerHand(), snapshot.getPlayerHand()));
        tableDisplay.drawHandsToPlay(ImageMap.ofHandsToSettle(snapshot.getHandsToPlay()));
    }

    private void renderConcealedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.getBet()));
        lblCards.setText(String.format("Cards Remaining: %s", snapshot.getDeckSize()));
        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(snapshot.getDealerHand()), score(snapshot.getPlayerHand()));
        tableDisplay.drawCards(ImageMap.ofConcealed(snapshot.getDealerHand(), snapshot.getPlayerHand()));
        tableDisplay.drawHandsToPlay(ImageMap.ofHandsToSettle(snapshot.getHandsToPlay()));
    }
}
