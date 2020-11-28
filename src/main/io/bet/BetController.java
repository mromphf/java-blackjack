package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import main.usecase.ControlListener;
import main.usecase.GameState;
import main.usecase.GameStateListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class BetController implements Initializable, GameStateListener {

    @FXML
    private Label lblBet;

    @FXML
    public Button btnDeal;

    @FXML
    public Button btnBet1;

    @FXML
    public Button btnBet5;

    @FXML
    public Button btnBet10;

    @FXML
    public Button btnBet25;

    @FXML
    public Button btnBet100;

    private final static int MAX_BET = 500;
    private final Collection<ControlListener> controlListeners;
    private int bet = 0;

    public BetController() {
        controlListeners = new ArrayList<>();
    }

    public void registerControlListener(ControlListener controlListener) {
        this.controlListeners.add(controlListener);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblBet.setFont(new Font("Arial", 50));
        btnBet1.setOnMouseClicked(event -> onBet(event, 1));
        btnBet5.setOnMouseClicked(event -> onBet(event, 5));
        btnBet10.setOnMouseClicked(event -> onBet(event, 10));
        btnBet25.setOnMouseClicked(event -> onBet(event, 25));
        btnBet100.setOnMouseClicked(event -> onBet(event, 100));
        btnDeal.setOnAction(event -> onPlay());
    }

    @Override
    public void onUpdate(GameState gameState) {
        this.bet = gameState.bet;
        btnDeal.setDisable(bet <= 0);
        lblBet.setText("Bet: $" + bet);
    }

    private void onBet(MouseEvent mouseEvent, int amount) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            int bet = Math.min(MAX_BET, this.bet + amount);
            controlListeners.forEach(cl -> cl.onBetPlaced(bet));
        } else {
            int bet = Math.max(0, this.bet - amount);
            controlListeners.forEach(cl -> cl.onBetPlaced(bet));
        }
        btnDeal.setDisable(bet <= 0);
        lblBet.setText("Bet: $" + bet);
    }

    private void onPlay() {
        controlListeners.forEach(ControlListener::onStartGame);
    }
}
