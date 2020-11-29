package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import main.io.RootController;
import main.usecase.ControlListener;
import main.domain.GameState;
import main.usecase.OutcomeListener;
import main.usecase.GameStateListener;

import java.net.URL;
import java.util.ResourceBundle;

import static main.domain.Rules.*;

public class BlackjackController extends RootController implements Initializable, GameStateListener, OutcomeListener {

    @FXML
    private Label lblBet;

    @FXML
    private Label lblBalance;

    @FXML
    private Label lblCards;

    @FXML
    private TableDisplay tableDisplay;

    @FXML
    private GridPane gameControls;

    @FXML
    private GridPane gameOverControls;

    @FXML
    private Button btnStand;

    @FXML
    private Button btnHit;

    @FXML
    private Button btnDouble;

    @FXML
    private Button btnNext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDouble.setOnAction(event -> onDouble());
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnNext.setOnAction(event -> controlListeners.forEach(ControlListener::onMoveToBettingTable));
    }

    @Override
    public void onUpdate(GameState gameState) {
        setGameButtonsDisabled(false);
        renderConcealedTable(gameState);
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);
        btnDouble.setDisable(gameState.isAtLeastOneCardDrawn());
        lblBalance.setText(String.format("Balance: $%s", gameState.getBalance()));
    }

    @Override
    public void onDealerWins(GameState gameState) {
        turnOffControls(gameState);
        tableDisplay.drawResults("Lose", Color.RED);
    }

    @Override
    public void onPlayerWins(GameState gameState) {
        turnOffControls(gameState);
        tableDisplay.drawResults("Win", Color.GREEN);
    }

    @Override
    public void onBust(GameState gameState) {
        turnOffControls(gameState);
        tableDisplay.drawResults("Bust", Color.RED);
    }

    @Override
    public void onPush(GameState gameState) {
        turnOffControls(gameState);
        tableDisplay.drawResults("Push", Color.ORANGE);
    }

    private void onDouble() {
        controlListeners.forEach(ControlListener::onDouble);
    }

    private void onStand() {
        controlListeners.forEach(ControlListener::onDealerTurn);
    }

    private void onHit() {
        controlListeners.forEach(ControlListener::onHit);
    }

    private void turnOffControls(GameState gameState) {
        setGameButtonsDisabled(true);
        renderExposedTable(gameState);
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);
    }

    private void renderExposedTable(GameState gameState) {
        lblBet.setText(String.format("Bet: $%s", gameState.getBet()));
        lblCards.setText(String.format("Cards Remaining: %s", gameState.getCardsRemaining()));
        tableDisplay.reset();
        tableDisplay.drawScores(score(gameState.getDealerHand()), score(gameState.getPlayerHand()));
        tableDisplay.drawCards(ImageMap.of(gameState.getDealerHand(), gameState.getPlayerHand()));
    }

    private void renderConcealedTable(GameState gameState) {
        lblBet.setText(String.format("Bet: $%s", gameState.getBet()));
        lblCards.setText(String.format("Cards Remaining: %s", gameState.getCardsRemaining()));
        tableDisplay.reset();
        tableDisplay.drawScores(concealedScore(gameState.getDealerHand()), score(gameState.getPlayerHand()));
        tableDisplay.drawCards(ImageMap.ofConcealed(gameState.getDealerHand(), gameState.getPlayerHand()));
    }

    private void setGameButtonsDisabled(boolean disabled) {
        btnHit.setDisable(disabled);
        btnDouble.setDisable(disabled);
        btnStand.setDisable(disabled);
    }
}
