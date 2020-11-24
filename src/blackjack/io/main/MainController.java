package blackjack.io.main;

import blackjack.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public Button btnPlay;

    @FXML
    public Button btnExit;

    public MainController(Main main, FXMLLoader fxmlLoader) throws IOException {
        fxmlLoader.setController(this);
        fxmlLoader.load();
        this.btnPlay.setOnAction(event -> main.switchToBetScreen());
        this.btnExit.setOnAction(event -> System.exit(1));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
