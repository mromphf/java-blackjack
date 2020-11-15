package blackjack.ui;

import blackjack.domain.Card;
import blackjack.domain.Deck;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        GraphicsContext context = foreground.getGraphicsContext2D();
        double screenHeight = screen.getHeight();
        double screenWidth = screen.getWidth();
        Collection<Card> deck = Deck.shuffle();
        Card c = deck.stream().iterator().next();
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        final Image img = new Image(String.format("file:graphics/%s.jpg", imageName));

        foreground.setHeight(screenHeight);
        foreground.setWidth(screenWidth);
        context.drawImage(img, 0, 0);
    }
}
