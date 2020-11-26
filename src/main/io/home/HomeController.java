package main.io.home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import main.AppRoot;
import main.Layout;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onPlay() {
        AppRoot.setLayout(Layout.BET);
    }

    @FXML void onExit() {
        System.exit(0);
    }
}
