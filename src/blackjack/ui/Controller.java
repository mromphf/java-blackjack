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
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        GraphicsContext context = foreground.getGraphicsContext2D();
        double screenHeight = screen.getHeight();
        double screenWidth = screen.getWidth();

        Stack<Card> deck = Deck.shuffle();
        Set<Card> dealerHand = new HashSet<>();
        Set<Card> playerHand = new HashSet<>();
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());

        Iterator<Card> player = playerHand.iterator();
        Iterator<Card> dealer = dealerHand.iterator();
        final Image img1 = imageName(dealer.next());
        final Image img2 = imageName(dealer.next());
        final Image img3 = imageName(player.next());
        final Image img4 = imageName(player.next());

        foreground.setHeight(screenHeight);
        foreground.setWidth(screenWidth);

        context.drawImage(img1, 800, 200);
        context.drawImage(img2, 1000, 200);
        context.drawImage(img3, 800, 600);
        context.drawImage(img4, 1000, 600);
    }

    private Image imageName(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return  new Image(String.format("file:graphics/%s.jpg", imageName));
    }
}
