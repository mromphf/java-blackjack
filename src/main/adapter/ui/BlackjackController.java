package main.adapter.ui;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import main.adapter.graphics.animation.DelayedSequence;
import main.adapter.graphics.animation.RevealSequence;
import main.adapter.graphics.animation.ImageRow;
import main.adapter.graphics.animation.TableDisplay;
import main.domain.model.TableView;
import main.usecase.Game;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.runLater;
import static main.adapter.graphics.VectorFunctions.*;
import static main.adapter.graphics.animation.DelayedSequence.delayedSequence;
import static main.adapter.graphics.animation.RevealSequence.revealSequence;
import static main.adapter.graphics.animation.ImageRow.imageRow;
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
        updateTable(game.submitAction(SPLIT));
    }

    @FXML
    public void onNoSplit() {
        splitControls.setVisible(false);
        gameControls.setVisible(true);
    }

    @FXML
    private void onStand() {
        updateTable(game.submitAction(STAND));
    }

    @FXML
    private void onSettleNextHand() {
        updateTable(game.submitAction(SETTLE));
    }

    @FXML
    private void onDone() {
        screenSupervisor.switchTo(BET);
        tableDisplay.reset();
    }

    @FXML
    private void onHit() {
        updateTable(game.submitAction(HIT));
    }

    @FXML
    private void onDouble() {
        updateTable(game.submitAction(DOUBLE));
    }

    @FXML
    private void onTakeInsurance() {
        updateTable(game.submitAction(BUY_INSURANCE));
    }

    @FXML
    private void onWaiveInsurance() {
        updateTable(game.submitAction(WAIVE_INSURANCE));
    }

    @FXML
    void onPlayNextHand() {
        updateTable(game.submitAction(NEXT));
    }

    public void updateTable(TableView tableView) {
        final boolean outcomeResolved = outcomeIsResolved.test(tableView);
        final int numDealerCards = tableView.dealerHand().size();
        final int numPlayerCards = tableView.playerHand().size();

        runLater(() -> {
            insuranceControls.setVisible(isInsuranceAvailable.test(tableView));
            gameControls.setVisible(isGameInProgress.test(tableView));
            splitControls.setVisible(isSplitAvailable.test(tableView));
            settleControls.setVisible(readyToSettleNextHand.test(tableView));
            nextHandControls.setVisible(readyToPlayNextHand.test(tableView));
            gameOverControls.setVisible(allBetsSettled.test(tableView));

            prgDeck.setProgress(tableView.deckProgress());

            btnDouble.setDisable(atLeastOneCardDrawn.test(tableView) || !tableView.canAffordToSpendMore());
            btnSplit.setDisable(!(isSplitAvailable.test(tableView) && tableView.canAffordToSpendMore()));

            lblBalance.setText(tableView.balanceText());

            lblBet.setText(String.format("Bet $%s", tableView.bet()));

            tableDisplay.reset();

            tableDisplay.drawCardsToPlay(images.fromCards(tableView.cardsToPlay(), outcomeResolved));


            if (startOfRound.test(tableView)) {
                DelayedSequence animation = delayedSequence(
                        context, vectorsDealCards(tableDisplay),
                        images.fromAllCards(tableView));

                new Thread(animation::start, "Deal Cards Animation Thread").start();

            } else if (timeForDealerReveal.test(tableView)) {
                RevealSequence animation = revealSequence(
                        context, vectorsDealerReveal(tableDisplay, numDealerCards),
                        center(tableDisplay),
                        tableView.outcome(),
                        images.fromDealerCards(tableView));

                ImageRow playerRow = imageRow(
                        context, vectorsPlayerRow(tableDisplay, numPlayerCards),
                        images.fromPlayerCards(tableView));

                playerRow.draw();

                new Thread(animation::start, "Dealer Reveal Animation Thread").start();
            } else {
                ImageRow playerRow = imageRow(
                        context, vectorsPlayerRow(tableDisplay, numPlayerCards),
                        images.fromPlayerCards(tableView));

                ImageRow dealerRow = imageRow(
                        context, vectorsDealerReveal(tableDisplay, numDealerCards),
                        images.fromDealerCards(tableView));

                playerRow.draw();
                dealerRow.draw();

                tableDisplay.drawResults(tableView.outcome());
            }
        });
    }
}
