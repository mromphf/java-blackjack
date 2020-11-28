package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import main.usecase.Round;
import main.usecase.RoundListener;

import java.net.URL;
import java.util.ResourceBundle;

public class BetController implements Initializable, RoundListener {

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
    private final Round round;

    public BetController(Round round) {
        this.round = round;
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
    public void onUpdate() {
        refresh();
    }

    private void onBet(MouseEvent mouseEvent, int amount) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            round.setBet(Math.min(MAX_BET, round.getBet() + amount));
        } else {
            round.setBet(Math.max(0, round.getBet() - amount));
        }
        refresh();
    }

    private void onPlay() {
        round.startGame();
        refresh();
    }

    private void refresh() {
        btnDeal.setDisable(round.getBet() <= 0);
        lblBet.setText("Bet: $" + round.getBet());
    }
}
