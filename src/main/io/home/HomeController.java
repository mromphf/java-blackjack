package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import main.usecase.Round;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    public Button btnPlay;

    @FXML
    public Button btnExit;

    private final Round round;

    public HomeController(Round round) {
        this.round = round;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPlay.setOnAction(event -> onPlay());
        btnExit.setOnAction(event -> onExit());
    }

    public void onPlay() {
        round.moveToBettingTable();
    }

    public void onExit() {
        System.exit(0);
    }
}
