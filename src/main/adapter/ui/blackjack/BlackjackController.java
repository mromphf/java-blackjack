package main.adapter.ui.blackjack;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import main.domain.Snapshot;
import main.usecase.Game;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.SnapshotListener;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static javafx.application.Platform.runLater;
import static main.domain.Action.*;
import static main.domain.Rules.concealedScore;
import static main.domain.Rules.score;
import static main.usecase.Layout.BET;

public class BlackjackController extends EventConnection implements Initializable, SnapshotListener {

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

    private final UUID key = randomUUID();
    private final Game game;

    @Inject
    public BlackjackController(Game game) {
        this.game = game;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

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
            lblBalance.setText(String.format("Balance $%s", snapshot.getBalance()));

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
        game.onActionTaken(SPLIT);
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        game.onActionTaken(STAND);
    }

    @FXML
    private void onSettleNextHand() {
        game.onActionTaken(SETTLE);
    }

    @FXML
    private void onDone() {
        eventNetwork.onLayoutEvent(BET);
    }

    @FXML
    private void onHit() {
        game.onActionTaken(HIT);
    }

    @FXML
    private void onDouble() {
        game.onActionTaken(DOUBLE);
    }

    @FXML
    private void onTakeInsurance() {
        game.onActionTaken(BUY_INSURANCE);
    }

    @FXML
    private void onWaiveInsurance() {
        game.onActionTaken(WAIVE_INSURANCE);
    }

    @FXML
    void onPlayNextHand() {
        game.onActionTaken(PLAY_NEXT_HAND);
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
