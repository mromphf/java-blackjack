package blackjack.ui;
import blackjack.Round;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Screen screen = new Screen(foreground);
        Map<IMAGE_KEY, Image> imageMap = Round.opening();
        screen.drawCards(imageMap);
    }
}
