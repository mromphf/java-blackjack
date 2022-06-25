package main.adapter.ui.blackjack;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.domain.model.Outcome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.scene.paint.Color.*;
import static main.adapter.ui.blackjack.OLD_IMAGE_KEY.DEALER_CARDS;
import static main.adapter.ui.blackjack.OLD_IMAGE_KEY.PLAYER_CARDS;
import static main.domain.model.Outcome.*;

public class TableDisplay extends Canvas {

    private final static Map<Outcome, String> OUTCOME_STRINGS = new HashMap<Outcome, String>() {{
        put(BLACKJACK, "Blackjack!!!");
        put(WIN, "Win");
        put(LOSE, "Lose");
        put(BUST, "Bust");
        put(PUSH, "Push");
    }};

    private final static Map<Outcome, Color> OUTCOME_COLORS = new HashMap<Outcome, Color>() {{
        put(BLACKJACK, WHITE);
        put(WIN, WHITE);
        put(LOSE, RED);
        put(BUST, RED);
        put(PUSH, ORANGE);
    }};

    private final static int TEXT_OFFSET = 50;

    private final int HOR_CENTER;
    private final int VER_CENTER;
    private final int CARD_WIDTH;
    private final int CARD_HEIGHT;
    private final int GAP_BETWEEN_CARDS;
    private final int BOTTOM;
    private final GraphicsContext context = getGraphicsContext2D();

    public TableDisplay() {
        final Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        setHeight((int) screen.getHeight() * 0.6);
        setWidth((int) screen.getWidth());
        HOR_CENTER = (int) getWidth() / 2;
        VER_CENTER = (int) getHeight() / 2;
        CARD_HEIGHT = (int) (screen.getHeight() * 0.175);
        CARD_WIDTH = (int) (screen.getWidth() * 0.08);
        GAP_BETWEEN_CARDS = (int) (screen.getWidth() * 0.15);
        BOTTOM = (int) getHeight();
    }

    public void reset() {
        context.clearRect(0, 0, getWidth(), getHeight());
        context.setFill(Color.valueOf("#228b22"));
        context.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawScores(int dealerScore, int playerScore) {
        drawLabel(String.format("Dealer: %s", dealerScore), 120);
        drawLabel(String.format("You: %s", playerScore), VER_CENTER + 130);
    }

    public void drawCards(Map<OLD_IMAGE_KEY, List<Image>> imageMap) {
        drawLineOfCards(imageMap.get(DEALER_CARDS), 100);
        drawLineOfCards(imageMap.get(PLAYER_CARDS), VER_CENTER + 110);
    }

    public void drawHandsToPlay(List<List<Image>> cards) {
        for (int i = 0, y = BOTTOM - 100; i < cards.size(); i++, y -= 120) {
            drawSmallLineOfCards(cards.get(i), y);
        }
    }

    public void drawResults(Outcome outcome) {
        final Font f = new Font("Arial", 50);
        context.setFont(f);
        context.setFill(OUTCOME_COLORS.get(outcome));
        context.fillText(OUTCOME_STRINGS.get(outcome), HOR_CENTER - 50, VER_CENTER + 20);
    }

    private void drawLabel(String label, int y) {
        final Font f = new Font("Arial", 30);
        context.setFont(f);
        context.setFill(WHITE);
        context.fillText(label, (HOR_CENTER - TEXT_OFFSET), y - 50);
    }

    private void drawLineOfCards(List<Image> cards, int y) {
        final int START_POS  = HOR_CENTER - (CARD_WIDTH * cards.size()) + (CARD_WIDTH / 2);

        for (int i = 0, x = START_POS; i < cards.size(); i++, x += GAP_BETWEEN_CARDS) {
            context.drawImage(cards.get(i), x, y, CARD_WIDTH, CARD_HEIGHT);
        }
    }

    private void drawSmallLineOfCards(List<Image> cards, int y) {
        for (int i = 0, x = 30; i < cards.size(); i++, x += GAP_BETWEEN_CARDS * 0.3) {
            context.drawImage(cards.get(i), x, y, CARD_WIDTH * 0.5, CARD_HEIGHT * 0.5);
        }
    }
}
