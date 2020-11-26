package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import main.AppRoot;
import main.Layout;

import java.net.URL;
import java.util.ResourceBundle;

public class BetController implements Initializable {

    @FXML
    private Label lblBet;

    @FXML
    Button btnDeal;

    private int bet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font f = new Font("Arial", 50);
        this.bet = 0;
        lblBet.setFont(f);
    }

    @FXML
    public void onBet5() {
        bet += 5;
        refresh();
    }

    @FXML
    public void onBet10() {
        bet += 10;
        refresh();
    }

    @FXML
    public void onBet25() {
        bet += 25;
        refresh();
    }

    @FXML
    public void onBet100() {
        bet += 100;
        refresh();
    }

    @FXML
    public void onPlay() {
        bet = 0;
        refresh();
        AppRoot.setLayout(Layout.GAME);
    }

    private void refresh() {
        btnDeal.setDisable(bet <= 0);
        lblBet.setText("Bet: $" + bet);
    }
}
