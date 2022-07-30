package main.adapter.ui;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import main.adapter.graphics.VectorFunctions;
import main.adapter.graphics.animation.DealCards;
import main.adapter.graphics.animation.DealerReveal;
import main.adapter.graphics.animation.TableDisplay;
import main.domain.model.Table;
import main.usecase.Game;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.runLater;
import static main.adapter.graphics.VectorFunctions.dealerReveal;
import static main.adapter.ui.Screen.BET;
import static main.domain.model.Action.*;
import static main.domain.predicate.LowOrderPredicate.*;

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
    private final ScreenManagement screenSupervisor;
    private final ImageService images;

    private GraphicsContext context;

    @Inject
    public BlackjackController(Game game,
                               ScreenManagement screenSupervisor,
                               ImageService images) {
        this.game = game;
        this.screenSupervisor = screenSupervisor;
        this.images = images;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.context = tableDisplay.getGraphicsContext2D();
        tableDisplay.setContext(context);
    }

    @Override
    public void onScreenChanged() {
        updateTable(game.peek());
    }

    @FXML
    public void onSplit() {
        updateTable(game.action(SPLIT));
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        updateTable(game.action(STAND));
    }

    @FXML
    private void onSettleNextHand() {
        updateTable(game.action(SETTLE));
    }

    @FXML
    private void onDone() {
        screenSupervisor.switchTo(BET);
        tableDisplay.reset();
    }

    @FXML
    private void onHit() {
        updateTable(game.action(HIT));
    }

    @FXML
    private void onDouble() {
        updateTable(game.action(DOUBLE));
    }

    @FXML
    private void onTakeInsurance() {
        updateTable(game.action(BUY_INSURANCE));
    }

    @FXML
    private void onWaiveInsurance() {
        updateTable(game.action(WAIVE_INSURANCE));
    }

    @FXML
    void onPlayNextHand() {
        updateTable(game.action(NEXT));
    }

    public void updateTable(Table table) {
        final boolean outcomeResolved = outcomeIsResolved.test(table);

        runLater(() -> {
            insuranceControls.setVisible(isInsuranceAvailable.test(table));
            gameControls.setVisible(isGameInProgress.test(table));
            splitControls.setVisible(isSplitAvailable.test(table));
            settleControls.setVisible(readyToSettleNextHand.test(table));
            nextHandControls.setVisible(readyToPlayNextHand.test(table));
            gameOverControls.setVisible(allBetsSettled.test(table));

            prgDeck.setProgress(table.deckProgress());

            btnDouble.setDisable(atLeastOneCardDrawn.test(table) || !table.canAffordToSpendMore());
            btnSplit.setDisable(!(isSplitAvailable.test(table) && table.canAffordToSpendMore()));

            lblBalance.setText(table.balanceText());

            lblBet.setText(String.format("Bet: $%s", table.bet()));

            tableDisplay.reset();

            tableDisplay.drawScores(table.dealerScore(), table.playerScore());

            tableDisplay.drawCardsToPlay(images.fromCards(table.cardsToPlay(), outcomeResolved));

            tableDisplay.drawResults(table.outcome());

            if (startOfRound.test(table)) {
                DealCards animation = new DealCards(
                        VectorFunctions.openingCardDeal(tableDisplay),
                        context,
                        images.fromCards(table.dealerHand(), outcomeResolved),
                        images.fromCards(table.playerHand(), outcomeResolved));

                new Thread(animation::start, "Deal Cards Animation Thread").start();
            } else if (timeForDealerReveal.test(table)) {
                DealerReveal animation = new DealerReveal(
                        context,
                        dealerReveal(tableDisplay, table.dealerHand().size()),
                        images.fromCards(table.dealerHand(), outcomeResolved));

                tableDisplay.drawPlayerCards(images.fromCards(table.playerHand(), outcomeResolved));
                tableDisplay.drawResults(table.outcome());

                new Thread(animation::start, "Dealer Reveal Animation Thread").start();
            } else {
                tableDisplay.drawCards(
                        images.fromCards(table.dealerHand(), outcomeResolved),
                        images.fromCards(table.playerHand(), outcomeResolved));
            }
        });
    }
}
