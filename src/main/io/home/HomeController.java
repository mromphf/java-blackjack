package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import main.io.RootController;
import main.usecase.ControlListener;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends RootController implements Initializable {

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
}
