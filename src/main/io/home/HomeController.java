package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import main.io.RootController;
import main.usecase.ControlListener;
import main.usecase.GameState;
import main.usecase.GameStateListener;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends RootController implements Initializable, GameStateListener {

    @FXML
    private Label lblBalance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onPlay() {
        controlListeners.forEach(ControlListener::onMoveToBettingTable);
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }

    @Override
    public void onUpdate(GameState gameState) {
        lblBalance.setText(String.format("Balance: $%s", gameState.balance));
    }
}
