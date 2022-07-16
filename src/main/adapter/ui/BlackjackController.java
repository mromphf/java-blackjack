package main.adapter.ui;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import main.usecase.Game;
import main.domain.model.TableView;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.runLater;
import static main.adapter.injection.Bindings.MAX_CARDS;
import static main.domain.model.Action.*;
import static main.domain.predicate.LowOrderPredicate.*;
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
    private final ImageMap images;

    @Inject
    public BlackjackController(Game game,
                               ScreenManagement screenSupervisor,
                               ImageMap images,
                               @Named(MAX_CARDS) int maxCards) {
        this.game = game;
        this.maxDeckSize = maxCards;
        this.screenSupervisor = screenSupervisor;
        this.images = images;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onScreenChanged() {
        onGameUpdate(game.peek());
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

    public void onGameUpdate(TableView tableView) {
        runLater(() -> {
            insuranceControls.setVisible(isInsuranceAvailable.test(tableView));
            gameControls.setVisible(isGameInProgress.test(tableView));
            splitControls.setVisible(isSplitAvailable.test(tableView));
            settleControls.setVisible(readyToSettleNextHand.test(tableView));
            nextHandControls.setVisible(readyToPlayNextHand.test(tableView));
            gameOverControls.setVisible(allBetsSettled.test(tableView));
            prgDeck.setProgress(tableView.deckProgress(maxDeckSize));
            btnDouble.setDisable(atLeastOneCardDrawn.test(tableView) || !tableView.canAffordToSpendMore());
            btnSplit.setDisable(!(isSplitAvailable.test(tableView) && tableView.canAffordToSpendMore()));
            lblBalance.setText(tableView.balanceText());

            lblBet.setText(String.format("Bet: $%s", tableView.bet()));

            tableDisplay.reset();

            tableDisplay.drawScores(
                    tableView.dealerScore(),
                    tableView.playerScore());

            tableDisplay.drawCards(
                    images.imageArray(tableView.dealerHand(), outcomeIsResolved.test(tableView)),
                    images.imageArray(tableView.playerHand(), outcomeIsResolved.test(tableView)));

            tableDisplay.drawCardsToPlay(images.fromCards(tableView.cardsToPlay()));
            tableDisplay.drawResults(tableView.outcome());
        });
    }
}
