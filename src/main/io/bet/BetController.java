package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.domain.Snapshot;
import main.io.RootController;
import main.usecase.ControlListener;
import main.usecase.GameStateListener;

import java.net.URL;
import java.util.ResourceBundle;

public class BetController extends RootController implements Initializable, GameStateListener {

    @FXML
    private Label lblBet;

    @FXML
    private Label lblBalance;

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
    private int bet = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnBet1.setOnMouseClicked(event -> onBet(event, 1));
        btnBet5.setOnMouseClicked(event -> onBet(event, 5));
        btnBet10.setOnMouseClicked(event -> onBet(event, 10));
        btnBet25.setOnMouseClicked(event -> onBet(event, 25));
        btnBet100.setOnMouseClicked(event -> onBet(event, 100));
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        this.bet = snapshot.getBet();
        btnDeal.setDisable(bet > snapshot.getBalance() || bet <= 0);
        lblBet.setText("Bet: $" + bet);
        lblBalance.setText(String.format("Balance: $%s", snapshot.getBalance()));
    }

    @FXML
    private void onDeal() {
        controlListeners.forEach(ControlListener::onStartNewRound);
    }

    @FXML void onQuit() {
        controlListeners.forEach(ControlListener::onStopPlaying);
    }

    private void onBet(MouseEvent mouseEvent, int amount) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            int bet = Math.min(MAX_BET, this.bet + amount);
            controlListeners.forEach(cl -> cl.onBetPlaced(bet));
        } else {
            int bet = Math.max(0, this.bet - amount);
            controlListeners.forEach(cl -> cl.onBetPlaced(bet));
        }
        lblBet.setText("Bet: $" + bet);
    }
}
