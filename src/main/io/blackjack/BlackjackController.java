package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.domain.Account;
import main.domain.Snapshot;
import main.io.EventConnection;
import main.usecase.BalanceListener;
import main.usecase.GameStateListener;

import java.net.URL;
import java.util.ResourceBundle;

import static main.usecase.Layout.BET;
import static main.domain.Action.*;
import static main.domain.Outcome.UNRESOLVED;
import static main.domain.Rules.*;

public class BlackjackController extends EventConnection implements Initializable, GameStateListener, BalanceListener {

    @FXML
    private Label lblBet;

    @FXML
    private Label lblBalance;

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

    @FXML
    private ProgressBar prgDeck;

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
        prgDeck.setProgress((double) snapshot.getDeckSize() / snapshot.getMaxCards());

        if (snapshot.isResolved()) {
            renderExposedTable(snapshot);

            switch (snapshot.getOutcome()) {
                case WIN:
                    tableDisplay.drawResults("Win", Color.WHITE);
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
        eventNetwork.onActionTaken(SPLIT);
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        eventNetwork.onActionTaken(STAND);
    }

    @FXML
    private void onSettleNextHand() {
        eventNetwork.onActionTaken(SETTLE);
    }

    @FXML
    private void onDone() {
        eventNetwork.onChangeLayout(BET);
    }

    @FXML
    private void onHit() {
        eventNetwork.onActionTaken(HIT);
    }

    @FXML
    private void onDouble() {
        eventNetwork.onActionTaken(DOUBLE);
    }

    @FXML
    private void onTakeInsurance() {
        eventNetwork.onActionTaken(BUY_INSURANCE);
    }

    @FXML
    private void onWaiveInsurance() {
        eventNetwork.onActionTaken(WAIVE_INSURANCE);
    }

    private void renderExposedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.getBet()));
        tableDisplay.reset();
        tableDisplay.drawScores(score(snapshot.getDealerHand()), score(snapshot.getPlayerHand()));
        tableDisplay.drawCards(ImageMap.of(snapshot.getDealerHand(), snapshot.getPlayerHand()));
        tableDisplay.drawHandsToPlay(ImageMap.ofHandsToSettle(snapshot.getHandsToPlay()));
    }

    private void renderConcealedTable(Snapshot snapshot) {
        lblBet.setText(String.format("Bet: $%s", snapshot.getBet()));
        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(snapshot.getDealerHand()), score(snapshot.getPlayerHand()));
        tableDisplay.drawCards(ImageMap.ofConcealed(snapshot.getDealerHand(), snapshot.getPlayerHand()));
        tableDisplay.drawHandsToPlay(ImageMap.ofHandsToSettle(snapshot.getHandsToPlay()));
    }
}
