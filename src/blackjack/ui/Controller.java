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

    private final String DLR_CARD_1 = "img1";
    private final String DLR_CARD_2 = "img2";
    private final String PLR_CARD_1 = "img3";
    private final String PLR_CARD_2 = "img4";

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

        Map<String, Image> imageMap = new HashMap<String, Image>() {{
            put(DLR_CARD_1, imageFile(dealer.next()));
            put(DLR_CARD_2, imageFile(dealer.next()));
            put(PLR_CARD_1, imageFile(player.next()));
            put(PLR_CARD_2, imageFile(player.next()));
        }};

        foreground.setHeight(screenHeight);
        foreground.setWidth(screenWidth);

        context.drawImage(imageMap.get(DLR_CARD_1), 800, 200);
        context.drawImage(imageMap.get(DLR_CARD_2), 1000, 200);
        context.drawImage(imageMap.get(PLR_CARD_1), 800, 600);
        context.drawImage(imageMap.get(PLR_CARD_2), 1000, 600);
    }

    private Image imageFile(Card c) {
        String imageName = c.getSuit().name().toLowerCase() + c.getValue();
        return new Image(String.format("file:graphics/%s.jpg", imageName));
    }
}
