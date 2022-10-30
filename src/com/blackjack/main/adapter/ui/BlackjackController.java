package com.blackjack.main.adapter.ui;

import com.blackjack.main.adapter.graphics.animation.AnimationFactory;
import com.blackjack.main.adapter.graphics.animation.TableDisplay;
import com.blackjack.main.domain.model.TableView;
import com.blackjack.main.usecase.Game;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.blackjack.main.adapter.ui.Screen.BET;
import static com.blackjack.main.domain.model.Action.*;
import static com.blackjack.main.domain.predicate.LowOrderPredicate.*;
import static javafx.application.Platform.runLater;

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

    private GraphicsContext graphics;

    @Inject
    public BlackjackController(
            Game game,
            ScreenManagement screenSupervisor,
            ImageService images) {
        this.game = game;
        this.screenSupervisor = screenSupervisor;
        this.images = images;

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.graphics = tableDisplay.getGraphicsContext2D();
        tableDisplay.setContext(graphics);
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

            tableDisplay.drawCardsToPlay(images.fromCards(tableView.cardsToPlay().stream()));

            render(tableView);
        });
    }

    private void render(TableView tableView) {
        final AnimationFactory animations = new AnimationFactory(graphics, tableDisplay, images);

        if (startOfRound.test(tableView)) {
            animations.dealAnimation(tableView.allCardsInPlay()).start();

        } else if (timeForDealerReveal.test(tableView)) {
            animations.playerImageRow(tableView).draw();
            animations.revealAnimation(tableView).start();

        } else {
            animations.playerImageRow(tableView).draw();
            animations.dealerImageRow(tableView).draw();

            tableDisplay.drawResults(tableView.outcome());
        }
    }
}
