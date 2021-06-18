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
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.SnapshotListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ResourceBundle;

import static main.domain.Action.*;
import static main.domain.Rules.concealedScore;
import static main.domain.Rules.score;
import static main.usecase.Layout.BET;
import static main.usecase.eventing.Predicate.*;

public class BlackjackController extends EventConnection implements Initializable, SnapshotListener, AccountListener {

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
    private Button btnSplit;

    @FXML
    private ProgressBar prgDeck;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onAccountEvent(Event<Account> event) {
        final int currentBalance = event.getData().getBalance();
        lblBalance.setText(String.format("Balance: $%s", currentBalance));
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(LocalDateTime.now(), event.getPredicate(), account)));
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        final int currentBalance = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED).getBalance();

        insuranceControls.setVisible(snapshot.isInsuranceAvailable());
        gameControls.setVisible(snapshot.isGameInProgress());
        splitControls.setVisible(snapshot.isSplitAvailable());
        settleControls.setVisible(snapshot.readyToSettleNextHand());
        nextHandControls.setVisible(snapshot.readyToPlayNextHand());
        gameOverControls.setVisible(snapshot.allBetsSettled());
        btnDouble.setDisable(!(snapshot.isAtLeastOneCardDrawn() && snapshot.canAffordToSpendMore(currentBalance)));
        btnSplit.setDisable(!(snapshot.isSplitAvailable() && snapshot.canAffordToSpendMore(currentBalance)));
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

    @FXML
    public void onSplit() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, SPLIT));
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, STAND));
    }

    @FXML
    private void onSettleNextHand() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, SETTLE));
    }

    @FXML
    private void onDone() {
        eventNetwork.onLayoutEvent(new Event<>(LocalDateTime.now(), LAYOUT_CHANGED, BET));
    }

    @FXML
    private void onHit() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, HIT));
    }

    @FXML
    private void onDouble() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, DOUBLE));
    }

    @FXML
    private void onTakeInsurance() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, BUY_INSURANCE));
    }

    @FXML
    private void onWaiveInsurance() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, WAIVE_INSURANCE));
    }

    @FXML void onPlayNextHand() {
        eventNetwork.onActionEvent(new Event<>(LocalDateTime.now(), ACTION_TAKEN, PLAY_NEXT_HAND));
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
