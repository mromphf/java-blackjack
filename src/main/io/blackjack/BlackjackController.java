package main.io.blackjack;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import main.usecase.GameState;
import main.usecase.RoundListener;
import main.usecase.Round;

import java.net.URL;
import java.util.ResourceBundle;

import static main.domain.Rules.*;

public class BlackjackController implements Initializable, RoundListener {

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

    private final Round round;

    public BlackjackController(Round round) {
        this.round = round;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDouble.setOnAction(event -> onDouble());
        btnHit.setOnAction(event -> onHit());
        btnStand.setOnAction(event -> onStand());
        btnNext.setOnAction(event -> round.moveToBettingTable());
    }

    @Override
    public void onUpdate(GameState gameState) {
        setGameButtonsDisabled(false);
        renderConcealedTable();
        gameControls.setVisible(true);
        gameOverControls.setVisible(false);

        btnDouble.setDisable(round.getHand("player").size() > 2);

        if (round.playerBusted()) {
            showdown();
        }
    }

    private void onDouble() {
        round.hit();
        round.dealerTurn();
        showdown();
    }

    private void onStand() {
        round.dealerTurn();
        showdown();
    }

    private void onHit() {
        round.hit();
    }

    private void showdown() {
        setGameButtonsDisabled(true);
        renderExposedTable();
        gameControls.setVisible(false);
        gameOverControls.setVisible(true);

        if (round.playerBusted()) {
            tableDisplay.drawResults("Bust", Color.RED);
        } else if (round.isPush()) {
            tableDisplay.drawResults("Push", Color.ORANGE);
        } else if (round.playerHasWon()) {
            tableDisplay.drawResults("Win", Color.GREEN);
        } else {
            tableDisplay.drawResults("Lose", Color.RED);
        }
    }

    private void renderExposedTable() {
        tableDisplay.reset();
        tableDisplay.drawBet(round.getBet());
        tableDisplay.drawDeck(new Image("file:graphics/card_blue.jpg"), round.numCardsRemaining());
        tableDisplay.drawScores(
                score(round.getHand("dealer")),
                score(round.getHand("player")) );
        tableDisplay.drawCards(
                ImageMap.of(
                        round.getHand("dealer"),
                        round.getHand("player")));
    }

    private void renderConcealedTable() {
        tableDisplay.reset();
        tableDisplay.drawBet(round.getBet());
        tableDisplay.drawDeck(new Image("file:graphics/card_blue.jpg"), round.numCardsRemaining());
        tableDisplay.drawScores(
                concealedScore(round.getHand("dealer")),
                score(round.getHand("player")));
        tableDisplay.drawCards(
                ImageMap.ofConcealed(
                        round.getHand("dealer"),
                        round.getHand("player")));
    }

    private void setGameButtonsDisabled(boolean disabled) {
        btnHit.setDisable(disabled);
        btnDouble.setDisable(disabled);
        btnStand.setDisable(disabled);
    }
}
