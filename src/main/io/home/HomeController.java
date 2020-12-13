package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.io.RootController;
import main.usecase.NavListener;
import main.usecase.SettlementListener;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends RootController implements Initializable, SettlementListener {

    @FXML
    private Label lblBalance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onPlay() {
        navListeners.forEach(NavListener::onMoveToBettingTable);
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }

    @Override
    public void onBalanceChanged(int balance) {
        lblBalance.setText(String.format("Balance: $%s", balance));
    }
}
