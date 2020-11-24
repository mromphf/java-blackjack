package main.io.bet;

import main.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BetController implements Initializable {

    @FXML
    private Button btnDeal;

    public BetController(Main main, FXMLLoader fxmlLoader) throws IOException {
        fxmlLoader.setController(this);
        fxmlLoader.load();
        btnDeal.setOnAction(event -> main.switchToBlackjackScreen());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
