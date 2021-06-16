package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.domain.Snapshot;
import main.io.EventConnection;
import main.usecase.BalanceListener;
import main.usecase.EventListener;
import main.usecase.GameStateListener;
import main.usecase.Message;

import java.net.URL;
import java.util.ResourceBundle;

import static main.usecase.Layout.BET;
import static main.domain.Action.*;
import static main.domain.Rules.*;
import static main.usecase.Layout.GAME;
import static main.usecase.Predicate.*;

public class BlackjackController extends EventConnection implements Initializable, GameStateListener, BalanceListener, EventListener {

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
    private GridPane nextHandControls;

    @FXML
    private GridPane splitControls;

    @FXML
    private Button btnDouble;

    @FXML
    private ProgressBar prgDeck;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onBalanceUpdated() {
        final int currentBalance = eventNetwork.fulfill(CURRENT_BALANCE).getCurrentBalance();
        lblBalance.setText(String.format("Balance: $%s", currentBalance));
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        insuranceControls.setVisible(snapshot.isInsuranceAvailable());
        gameControls.setVisible(snapshot.isGameInProgress());
        splitControls.setVisible(snapshot.isSplitAvailable());
        settleControls.setVisible(snapshot.readyToSettleNextHand());
        nextHandControls.setVisible(snapshot.readyToPlayNextHand());
        gameOverControls.setVisible(snapshot.allBetsSettled());
        btnDouble.setDisable(snapshot.isAtLeastOneCardDrawn());
        prgDeck.setProgress(snapshot.getDeckProgress());

        if (snapshot.isRoundResolved()) {
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

    @Override
    public void onEvent(Message message) {
        if (message.is(LAYOUT_CHANGED) && (message.getLayout().equals(GAME))) {
            onBalanceUpdated();
        }
    }

    @FXML
    public void onSplit() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, SPLIT));
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, STAND));
    }

    @FXML
    private void onSettleNextHand() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, SETTLE));
    }

    @FXML
    private void onDone() {
        eventNetwork.onEvent(Message.of(LAYOUT_CHANGED, BET));
    }

    @FXML
    private void onHit() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, HIT));
    }

    @FXML
    private void onDouble() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, DOUBLE));
    }

    @FXML
    private void onTakeInsurance() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, BUY_INSURANCE));
    }

    @FXML
    private void onWaiveInsurance() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, WAIVE_INSURANCE));
    }

    @FXML void onPlayNextHand() {
        eventNetwork.onEvent(Message.of(ACTION_TAKEN, PLAY_NEXT_HAND));
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
