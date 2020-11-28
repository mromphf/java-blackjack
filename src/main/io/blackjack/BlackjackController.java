package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import main.usecase.ControlListener;
import main.usecase.GameState;
import main.usecase.OutcomeListener;
import main.usecase.RoundListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import static main.domain.Rules.*;

public class BlackjackController implements Initializable, RoundListener, OutcomeListener {

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

    private final Collection<ControlListener> controlListeners;

    public BlackjackController() {
        this.controlListeners = new ArrayList<>();
    }

    public void registerControlListener(ControlListener controlListener) {
        controlListeners.add(controlListener);
    }

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
        btnDouble.setDisable(gameState.cardDrawn);
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
        controlListeners.forEach(ControlListener::onHit);
        controlListeners.forEach(ControlListener::onDealerTurn);
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
        tableDisplay.reset();
        tableDisplay.drawBet(gameState.bet);
        tableDisplay.drawDeck(new Image("file:graphics/card_blue.jpg"), gameState.cardsRemaining);
        tableDisplay.drawDeck(new Image("file:graphics/card_blue.jpg"), gameState.cardsRemaining);
        tableDisplay.drawScores(score(gameState.dealerHand), score(gameState.playerHand));
        tableDisplay.drawCards(ImageMap.of(gameState.dealerHand, gameState.playerHand));
    }

    private void renderConcealedTable(GameState gameState) {
        tableDisplay.reset();
        tableDisplay.drawBet(gameState.bet);
        tableDisplay.drawDeck(new Image("file:graphics/card_blue.jpg"), gameState.cardsRemaining);
        tableDisplay.drawScores(concealedScore(gameState.dealerHand), score(gameState.playerHand));
        tableDisplay.drawCards(ImageMap.ofConcealed( gameState.dealerHand, gameState.playerHand));
    }

    private void setGameButtonsDisabled(boolean disabled) {
        btnHit.setDisable(disabled);
        btnDouble.setDisable(disabled);
        btnStand.setDisable(disabled);
    }
}
