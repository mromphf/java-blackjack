package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.AppRoot;
import main.Layout;

import java.net.URL;
import java.util.ResourceBundle;

public class BetController implements Initializable {

    @FXML
    private Button btnDeal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDeal.setOnAction(event -> AppRoot.setLayout(Layout.GAME));
    }
}
