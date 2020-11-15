package blackjack.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Map;

public class Screen {

    private final GraphicsContext context;

    public Screen(Canvas foreground) {
        context = foreground.getGraphicsContext2D();
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
        foreground.setHeight(screen.getHeight());
        foreground.setWidth(screen.getWidth());
    }

    public void drawCards(Map<IMAGE_KEY, Image> imageMap) {
        context.drawImage(imageMap.get(IMAGE_KEY.DLR_CARD_1), 800, 200);
        context.drawImage(imageMap.get(IMAGE_KEY.DLR_CARD_2), 1000, 200);
        context.drawImage(imageMap.get(IMAGE_KEY.PLR_CARD_1), 800, 600);
        context.drawImage(imageMap.get(IMAGE_KEY.PLR_CARD_2), 1000, 600);
    }
}
