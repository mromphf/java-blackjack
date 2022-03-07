package main.adapter.ui.blackjack;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import main.domain.Account;
import main.domain.Snapshot;
import main.usecase.AccountCache;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.SnapshotListener;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static javafx.application.Platform.runLater;
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

    private final AccountCache accountCache;
    private final UUID key = randomUUID();

    @Inject
    public BlackjackController(AccountCache accountCache) {
        this.accountCache = accountCache;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(CURRENT_BALANCE_UPDATED)) {
            int currentBalance = event.getData().getBalance();
            runLater(() -> lblBalance.setText(String.format("Balance $%s", currentBalance)));
        }
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        runLater(() -> {
            insuranceControls.setVisible(snapshot.isInsuranceAvailable());
            gameControls.setVisible(snapshot.isGameInProgress());
            splitControls.setVisible(snapshot.isSplitAvailable());
            settleControls.setVisible(snapshot.readyToSettleNextHand());
            nextHandControls.setVisible(snapshot.readyToPlayNextHand());
            gameOverControls.setVisible(snapshot.allBetsSettled());
            prgDeck.setProgress(snapshot.getDeckProgress());
            btnDouble.setDisable(snapshot.isAtLeastOneCardDrawn() || !snapshot.canAffordToSpendMore());
            btnSplit.setDisable(!(snapshot.isSplitAvailable() && snapshot.canAffordToSpendMore()));

            if (snapshot.isRoundResolved()) {
                renderExposedTable(snapshot);
                tableDisplay.drawResults(snapshot.getOutcome());
            } else {
                renderConcealedTable(snapshot);
            }
        });
    }

    @FXML
    public void onSplit() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, SPLIT));
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, STAND));
    }

    @FXML
    private void onSettleNextHand() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, SETTLE));
    }

    @FXML
    private void onDone() {
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, BET));
    }

    @FXML
    private void onHit() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, HIT));
    }

    @FXML
    private void onDouble() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, DOUBLE));
    }

    @FXML
    private void onTakeInsurance() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, BUY_INSURANCE));
    }

    @FXML
    private void onWaiveInsurance() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, WAIVE_INSURANCE));
    }

    @FXML void onPlayNextHand() {
        eventNetwork.onActionEvent(new Event<>(key, now(), ACTION_TAKEN, PLAY_NEXT_HAND));
    }

    @Override
    public UUID getKey() {
        return key;
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
