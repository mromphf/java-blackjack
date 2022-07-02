package main.adapter.ui.blackjack;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import main.domain.Game;
import main.domain.model.Snapshot;
import main.adapter.ui.ScreenManagement;
import main.adapter.ui.ScreenObserver;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.runLater;
import static main.adapter.injection.Bindings.MAX_CARDS;
import static main.adapter.ui.blackjack.ImageMap.imageArray;
import static main.domain.model.Action.*;
import static main.domain.predicate.RoundPredicate.*;
import static main.adapter.ui.Screen.BET;

public class BlackjackController implements Initializable, ScreenObserver {

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

    private final Game game;
    private final float maxDeckSize;
    private final ScreenManagement screenSupervisor;

    @Inject
    public BlackjackController(Game game, ScreenManagement screenSupervisor, @Named(MAX_CARDS) int maxCards) {
        this.game = game;
        this.maxDeckSize = maxCards;
        this.screenSupervisor = screenSupervisor;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onScreenChanged() {
        onGameUpdate(game.start());
    }

    @FXML
    public void onSplit() {
        onGameUpdate(game.onActionTaken(SPLIT));
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        onGameUpdate(game.onActionTaken(STAND));
    }

    @FXML
    private void onSettleNextHand() {
        onGameUpdate(game.onActionTaken(SETTLE));
    }

    @FXML
    private void onDone() {
        screenSupervisor.switchTo(BET);
        tableDisplay.reset();
    }

    @FXML
    private void onHit() {
        onGameUpdate(game.onActionTaken(HIT));
    }

    @FXML
    private void onDouble() {
        onGameUpdate(game.onActionTaken(DOUBLE));
    }

    @FXML
    private void onTakeInsurance() {
        onGameUpdate(game.onActionTaken(BUY_INSURANCE));
    }

    @FXML
    private void onWaiveInsurance() {
        onGameUpdate(game.onActionTaken(WAIVE_INSURANCE));
    }

    @FXML
    void onPlayNextHand() {
        onGameUpdate(game.onActionTaken(PLAY_NEXT_HAND));
    }

    public void onGameUpdate(Snapshot snapshot) {
        runLater(() -> {
            insuranceControls.setVisible(isInsuranceAvailable.test(snapshot));
            gameControls.setVisible(isGameInProgress.test(snapshot));
            splitControls.setVisible(isSplitAvailable.test(snapshot));
            settleControls.setVisible(readyToSettleNextHand.test(snapshot));
            nextHandControls.setVisible(readyToPlayNextHand.test(snapshot));
            gameOverControls.setVisible(allBetsSettled.test(snapshot));
            prgDeck.setProgress(snapshot.deckProgress(maxDeckSize));
            btnDouble.setDisable(atLeastOneCardDrawn.test(snapshot) || !snapshot.canAffordToSpendMore());
            btnSplit.setDisable(!(isSplitAvailable.test(snapshot) && snapshot.canAffordToSpendMore()));
            lblBalance.setText(snapshot.balanceText());

            lblBet.setText(String.format("Bet: $%s", snapshot.getBet()));

            tableDisplay.reset();

            tableDisplay.drawScores(
                    snapshot.dealerScore(),
                    snapshot.playerScore());

            tableDisplay.drawCards(
                    imageArray(snapshot.getDealerHand(), outcomeIsResolved.test(snapshot)),
                    imageArray(snapshot.getPlayerHand(), outcomeIsResolved.test(snapshot)));

            tableDisplay.drawHandsToPlay(
                    ImageMap.ofHandsToSettle(snapshot.getHandsToPlay()));
            tableDisplay.drawResults(snapshot.getOutcome());
        });
    }
}
