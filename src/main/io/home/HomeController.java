package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import main.io.RootController;
import main.usecase.ControlListener;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends RootController implements Initializable {

    @FXML
    public Button btnPlay;

    @FXML
    public Button btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPlay.setOnAction(event -> onPlay());
        btnExit.setOnAction(event -> onExit());
    }

    public void onPlay() {
        controlListeners.forEach(ControlListener::onMoveToBettingTable);
    }

    public void onExit() {
        System.exit(0);
    }
}
