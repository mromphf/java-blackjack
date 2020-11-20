package blackjack.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Map;

public class Screen {

    private final static int CARD_WIDTH = 125;
    private final static int CARD_HEIGHT = 150;
    private final static int FOREGROUND_DIM = 800;

    private final GraphicsContext context;

    public Screen(Canvas foreground) {
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        foreground.setHeight(FOREGROUND_DIM);
        foreground.setWidth(FOREGROUND_DIM);

        context = foreground.getGraphicsContext2D();

        context.setFill(Color.WHITE);
        context.fillRect(0, 0, foreground.getWidth(), foreground.getHeight());

        context.setFill(Color.BLACK);
        context.rect(0, 0, foreground.getWidth(), foreground.getHeight());
        context.stroke();
    }

    public void drawCards(Map<IMAGE_KEY, Image> imageMap) {
        context.drawImage(imageMap.get(IMAGE_KEY.DLR_CARD_1), 225, 100, CARD_WIDTH, CARD_HEIGHT);
        context.drawImage(imageMap.get(IMAGE_KEY.DLR_CARD_2), 450, 100, CARD_WIDTH, CARD_HEIGHT);
        context.drawImage(imageMap.get(IMAGE_KEY.PLR_CARD_1), 225, 400, CARD_WIDTH, CARD_HEIGHT);
        context.drawImage(imageMap.get(IMAGE_KEY.PLR_CARD_2), 450, 400, CARD_WIDTH, CARD_HEIGHT);
    }
}
