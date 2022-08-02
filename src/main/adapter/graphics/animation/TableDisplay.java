package main.adapter.graphics.animation;

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
import static main.domain.model.Outcome.*;

public class TableDisplay extends Canvas {

    public final static Map<Outcome, String> OUTCOME_STRINGS = new HashMap<Outcome, String>() {{
        put(BLACKJACK, "Blackjack!");
        put(WIN, "Win");
        put(LOSE, "Lose");
        put(BUST, "Bust");
        put(PUSH, "Push");
    }};

    public final static Map<Outcome, Color> OUTCOME_COLORS = new HashMap<Outcome, Color>() {{
        put(BLACKJACK, WHITE);
        put(WIN, WHITE);
        put(LOSE, RED);
        put(BUST, RED);
        put(PUSH, ORANGE);
    }};

    private final static String TABLE_GREEN = "#228b22";
    private final static String FONT_NAME = "Arial";

    private final int HOR_CENTER;
    private final int VER_CENTER;
    private final int CARD_WIDTH;
    private final int CARD_HEIGHT;
    private final int BOTTOM;
    private GraphicsContext context;

    public TableDisplay() {
        final Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();

        setHeight((int) screen.getHeight() * 0.7);
        setWidth((int) screen.getWidth());
        HOR_CENTER = (int) getWidth() / 2;
        VER_CENTER = (int) getHeight() / 2;
        CARD_HEIGHT = (int) (screen.getHeight() * 0.175);
        CARD_WIDTH = (int) (screen.getWidth() * 0.08);
        BOTTOM = (int) getHeight();
    }

    public void setContext(GraphicsContext context) {
        this.context = context;
    }

    public void reset() {
        context.clearRect(0, 0, getWidth(), getHeight());
        context.setFill(Color.valueOf(TABLE_GREEN));
        context.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawCardsToPlay(List<Image> images) {
        for (int i = 0, y = BOTTOM - 100; i < images.size(); i++, y -= 120) {
            drawSmallCard(images.get(i), y);
        }
    }

    public void drawResults(Outcome outcome) {
        final Font f = new Font(FONT_NAME, 50);
        context.setFont(f);
        context.setFill(OUTCOME_COLORS.get(outcome));
        context.fillText(OUTCOME_STRINGS.get(outcome), HOR_CENTER - 50, VER_CENTER + 50);
    }

    private void drawSmallCard(Image card, int y) {
        context.drawImage(card, 30, y, CARD_WIDTH * 0.5, CARD_HEIGHT * 0.5);
    }
}
