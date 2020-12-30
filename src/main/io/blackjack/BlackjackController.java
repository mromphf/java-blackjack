package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.domain.Account;
import main.domain.Snapshot;
import main.io.RootController;
import main.usecase.BalanceListener;
import main.usecase.GameStateListener;

import java.net.URL;
import java.util.ResourceBundle;

import static main.Layout.BET;
import static main.domain.Action.*;
import static main.domain.Outcome.UNRESOLVED;
import static main.domain.Rules.*;

public class BlackjackController extends RootController implements Initializable, GameStateListener, BalanceListener {

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
    public void onBalanceUpdated(Account account) {
        lblBalance.setText(String.format("Balance: $%s", account.getBalance()));
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        insuranceControls.setVisible(snapshot.isInsuranceAvailable());
        gameControls.setVisible(!canSplit(snapshot.getPlayerHand()) && snapshot.is(UNRESOLVED) && !snapshot.isInsuranceAvailable());
        splitControls.setVisible(canSplit(snapshot.getPlayerHand()) && snapshot.is(UNRESOLVED) && !snapshot.isInsuranceAvailable());
        settleControls.setVisible(snapshot.isResolved() && !snapshot.isRoundFinished());
        gameOverControls.setVisible(snapshot.isResolved() && snapshot.isRoundFinished());
        btnDouble.setDisable(snapshot.isAtLeastOneCardDrawn());

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
        actionListeners.forEach(l -> l.onActionTaken(SPLIT));
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        actionListeners.forEach(l -> l.onActionTaken(STAND));
    }

    @FXML
    private void onSettleNextHand() {
        actionListeners.forEach(l -> l.onActionTaken(SETTLE));
    }

    @FXML
    private void onDone() {
        navListeners.forEach(l -> l.onChangeLayout(BET));
    }

    @FXML
    private void onHit() {
        actionListeners.forEach(l -> l.onActionTaken(HIT));
    }

    @FXML
    private void onDouble() {
        actionListeners.forEach(l -> l.onActionTaken(DOUBLE));
    }

    @FXML
    private void onTakeInsurance() {
        actionListeners.forEach(l -> l.onActionTaken(BUY_INSURANCE));
    }

    @FXML
    private void onNoInsurance() {
        actionListeners.forEach(l -> l.onActionTaken(NO_INSURANCE));
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
