package blackjack.io;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Map;

import static blackjack.io.IMAGE_KEY.DEALER_CARDS;
import static blackjack.io.IMAGE_KEY.PLAYER_CARDS;

public class Screen {

    private final static Rectangle2D SCREEN = javafx.stage.Screen.getPrimary().getBounds();
    private final static int CARD_WIDTH = 125;
    private final static int CARD_HEIGHT = 150;
    private final static int GAP_BETWEEN_CARDS = 225;
    private final static int SCREEN_HEIGHT = (int) SCREEN.getHeight();
    private final static int SCREEN_WIDTH = (int) SCREEN.getWidth();
    private final static int TEXT_OFFSET = 75;

    private final Canvas foreground;
    private final GraphicsContext context;

    public Screen(Canvas foreground) {
        this.foreground = foreground;
        this.foreground.setHeight(SCREEN_HEIGHT * 0.6);
        this.foreground.setWidth(SCREEN_WIDTH * 0.6);

        context = foreground.getGraphicsContext2D();

        context.setFill(Color.WHITE);
        context.fillRect(0, 0, foreground.getWidth(), foreground.getHeight());

        context.setFill(Color.BLACK);
        context.rect(0, 0, foreground.getWidth(), foreground.getHeight());
        context.stroke();
    }

    public void drawCards(Map<IMAGE_KEY, List<Image>> imageMap) {
        drawLineOfCards("Dealer's Hand", imageMap.get(DEALER_CARDS), 100);
        drawLineOfCards("Your Hand", imageMap.get(PLAYER_CARDS), 450);
    }

    public void blackScreen() {
        context.fillRect(0, 0, foreground.getWidth(), foreground.getHeight());
    }

    private void drawLineOfCards(String label, List<Image> cards, int y) {
        final Font f = new Font("Arial", 30);
        final int HOR_CENTER = (int) (foreground.getWidth() / 2);
        final int START_POS  = HOR_CENTER - (CARD_WIDTH * cards.size()) + CARD_WIDTH;

        context.setFont(f);

        context.fillText(label, (HOR_CENTER - TEXT_OFFSET), y - 50);

        for (int i = 0, x = START_POS; i < cards.size(); i++, x += GAP_BETWEEN_CARDS) {
            context.drawImage(cards.get(i), x, y, CARD_WIDTH, CARD_HEIGHT);
        }
    }
}
