package blackjack.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    private final Image img = new Image("file:graphics/clubs02.jpg");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        GraphicsContext context = foreground.getGraphicsContext2D();
        double screenHeight = screen.getHeight();
        double screenWidth = screen.getWidth();

        foreground.setHeight(screenHeight);
        foreground.setWidth(screenWidth);
        context.drawImage(img, 300, 300);
    }
}
