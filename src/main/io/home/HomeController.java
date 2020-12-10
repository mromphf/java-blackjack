package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import main.domain.Snapshot;
import main.io.RootController;
import main.usecase.GameStateListener;
import main.usecase.NavListener;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends RootController implements Initializable, GameStateListener {

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
    public void onUpdate(int balance, Snapshot snapshot) {
        lblBalance.setText(String.format("Balance: $%s", balance));
    }
}
