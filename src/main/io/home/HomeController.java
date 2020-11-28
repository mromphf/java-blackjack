package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import main.usecase.ControlListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    public Button btnPlay;

    @FXML
    public Button btnExit;

    private final Collection<ControlListener> controlListeners;

    public HomeController() {
        this.controlListeners = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPlay.setOnAction(event -> onPlay());
        btnExit.setOnAction(event -> onExit());
    }

    public void registerControlListener(ControlListener controlListener) {
        controlListeners.add(controlListener);
    }

    public void onPlay() {
        controlListeners.forEach(ControlListener::onMoveToBettingTable);
    }

    public void onExit() {
        System.exit(0);
    }
}
