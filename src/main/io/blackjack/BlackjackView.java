package main.io.blackjack;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Map;

import static main.io.blackjack.ImageKey.DEALER_CARDS;
import static main.io.blackjack.ImageKey.PLAYER_CARDS;

public class BlackjackView {

    private final static Rectangle2D SCREEN = javafx.stage.Screen.getPrimary().getBounds();
    private final static int CARD_WIDTH = 125;
    private final static int CARD_HEIGHT = 150;
    private final static int GAP_BETWEEN_CARDS = 225;
    private final static int SCREEN_HEIGHT = (int) SCREEN.getHeight();
    private final static int SCREEN_WIDTH = (int) SCREEN.getWidth();
    private final static int TEXT_OFFSET = 50;

    private final int HOR_CENTER;
    private final int VER_CENTER;
    private final Canvas foreground;
    private final GraphicsContext context;

    public BlackjackView(Canvas foreground) {
        this.foreground = foreground;
        this.foreground.setHeight(SCREEN_HEIGHT * 0.6);
        this.foreground.setWidth(SCREEN_WIDTH);
        HOR_CENTER = (int) foreground.getWidth() / 2;
        VER_CENTER = (int) foreground.getHeight() / 2;
        context = foreground.getGraphicsContext2D();
    }

    public void drawLabels(int dealerScore, int playerScore) {
        drawLabel(String.format("Dealer: %s", dealerScore), 100);
        drawLabel(String.format("You: %s", playerScore), 450);
    }

    public void drawCards(Map<ImageKey, List<Image>> imageMap) {
        drawLineOfCards(imageMap.get(DEALER_CARDS), 100);
        drawLineOfCards(imageMap.get(PLAYER_CARDS), 450);
    }

    public void push() {
        final Font f = new Font("Arial", 150);
        context.setFont(f);
        context.setFill(Color.ORANGE);
        context.fillText("PUSH", HOR_CENTER - 200, VER_CENTER);
    }

    public void bust() {
        final Font f = new Font("Arial", 150);
        context.setFont(f);
        context.setFill(Color.RED);
        context.fillText("BUST!", HOR_CENTER - 200, VER_CENTER);
    }

    public void reset() {
        context.clearRect(0, 0, foreground.getWidth(), foreground.getHeight());

        context.setFill(Color.WHITE);
        context.fillRect(0, 0, foreground.getWidth(), foreground.getHeight());

        context.setFill(Color.BLACK);
        context.rect(0, 0, foreground.getWidth(), foreground.getHeight());
        context.stroke();
    }

    private void drawLabel(String label, int y) {
        final Font f = new Font("Arial", 30);
        context.setFont(f);
        context.fillText(label, (HOR_CENTER - TEXT_OFFSET), y - 50);
    }

    private void drawLineOfCards(List<Image> cards, int y) {
        final int START_POS  = HOR_CENTER - (CARD_WIDTH * cards.size()) + (CARD_WIDTH / 2);

        for (int i = 0, x = START_POS; i < cards.size(); i++, x += GAP_BETWEEN_CARDS) {
            context.drawImage(cards.get(i), x, y, CARD_WIDTH, CARD_HEIGHT);
        }
    }
}
