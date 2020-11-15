package blackjack.ui;

import blackjack.domain.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;

import static blackjack.domain.Deck.*;

public class Controller implements Initializable {

    @FXML
    private Canvas foreground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        GraphicsContext context = foreground.getGraphicsContext2D();
        double screenHeight = screen.getHeight();
        double screenWidth = screen.getWidth();

        Stack<Card> deck = shuffle(fresh());
        Set<Card> dealerHand = new HashSet<>();
        Set<Card> playerHand = new HashSet<>();
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());
        playerHand.add(deck.pop());
        dealerHand.add(deck.pop());

        Iterator<Card> player = playerHand.iterator();
        Iterator<Card> dealer = dealerHand.iterator();
        Map<String, Image> imageMap = new HashMap<>();

        imageMap.put("img1", imageFile(dealer.next()));
        imageMap.put("img2", imageFile(dealer.next()));
        imageMap.put("img3", imageFile(player.next()));
        imageMap.put("img4", imageFile(player.next()));

        foreground.setHeight(screenHeight);
        foreground.setWidth(screenWidth);

        context.drawImage(imageMap.get("img1"), 800, 200);
        context.drawImage(imageMap.get("img2"), 1000, 200);
        context.drawImage(imageMap.get("img3"), 800, 600);
        context.drawImage(imageMap.get("img4"), 1000, 600);
    }

    private Image imageFile(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return  new Image(String.format("file:graphics/%s.jpg", imageName));
    }
}
