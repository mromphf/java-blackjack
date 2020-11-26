package main.io.main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.AppRoot;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public Button btnPlay;

    @FXML
    public Button btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPlay.setOnAction(event -> AppRoot.switchToBetScreen());
        btnExit.setOnAction(event -> System.exit(0));
    }
}
