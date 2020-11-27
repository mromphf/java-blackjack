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

public class TableDisplay extends Canvas {

    private final static int TEXT_OFFSET = 50;

    private final int HOR_CENTER;
    private final int VER_CENTER;
    private final int CARD_WIDTH;
    private final int CARD_HEIGHT;
    private final int GAP_BETWEEN_CARDS;
    private final GraphicsContext context = getGraphicsContext2D();

    public TableDisplay() {
        final Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        setHeight((int) screen.getHeight() * 0.6);
        setWidth((int) screen.getWidth());
        HOR_CENTER = (int) getWidth() / 2;
        VER_CENTER = (int) getHeight() / 2;
        CARD_HEIGHT = (int) (screen.getHeight() * 0.13);
        CARD_WIDTH = (int) (screen.getWidth() * 0.08);
        GAP_BETWEEN_CARDS = (int) (screen.getWidth() * 0.15);
    }

    public void reset() {
        context.clearRect(0, 0, getWidth(), getHeight());

        context.setFill(Color.WHITE);
        context.fillRect(0, 0, getWidth(), getHeight());

        context.setFill(Color.BLACK);
        context.rect(0, 0, getWidth(), getHeight());
        context.stroke();
    }

    public void drawScores(int dealerScore, int playerScore) {
        drawLabel(String.format("Dealer: %s", dealerScore), 100);
        drawLabel(String.format("You: %s", playerScore), VER_CENTER + 50);
    }

    public void drawCards(Map<ImageKey, List<Image>> imageMap) {
        drawLineOfCards(imageMap.get(DEALER_CARDS), 100);
        drawLineOfCards(imageMap.get(PLAYER_CARDS), VER_CENTER + 50);
    }

    public void drawResults(String text, Color color) {
        final Font f = new Font("Arial", 150);
        context.setFont(f);
        context.setFill(color);
        context.fillText(text, HOR_CENTER - 300, VER_CENTER);
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
