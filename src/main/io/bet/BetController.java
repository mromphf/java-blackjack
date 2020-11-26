package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import main.AppRoot;
import main.Layout;

import java.net.URL;
import java.util.ResourceBundle;

public class BetController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onPlay() {
        AppRoot.setLayout(Layout.GAME);
    }
}
