package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import main.BetListener;
import main.usecase.Round;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class BetController implements Initializable {

    @FXML
    private Label lblBet;

    @FXML
    public Button btnDeal;

    @FXML
    public Button btnBet5;

    @FXML
    public Button btnBet10;

    @FXML
    public Button btnBet25;

    @FXML
    public Button btnBet100;

    private final Collection<BetListener> betListeners;
    private final Round round;
    private int bet;

    public BetController(Round round, Collection<BetListener> betListeners) {
        this.bet = 0;
        this.round = round;
        this.betListeners = betListeners;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblBet.setFont(new Font("Arial", 50));
        btnBet5.setOnAction(event -> onBet(5));
        btnBet10.setOnAction(event -> onBet(10));
        btnBet25.setOnAction(event -> onBet(25));
        btnBet100.setOnAction(event -> onBet(100));
        btnDeal.setOnAction(event -> onPlay());
    }

    public void onBet(int amount) {
        bet += amount;
        refresh();
    }

    @FXML
    public void onPlay() {
        round.start(bet, betListeners);
        bet = 0;
        refresh();
    }

    private void refresh() {
        btnDeal.setDisable(bet <= 0);
        lblBet.setText("Bet: $" + bet);
    }
}
