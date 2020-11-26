package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.AppRoot;
import main.Layout;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    public Button btnPlay;

    @FXML
    public Button btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPlay.setOnAction(event -> AppRoot.setLayout(Layout.BET));
        btnExit.setOnAction(event -> System.exit(0));
    }
}
