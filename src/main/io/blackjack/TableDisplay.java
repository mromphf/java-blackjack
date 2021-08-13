package main.io.blackjack;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.io.graphics.AnimationCallback;
import main.io.graphics.OpeningHandsAnimation;
import main.io.graphics.TravelingImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javafx.scene.paint.Color.*;
import static main.io.blackjack.ImageKey.DEALER_CARDS;
import static main.io.blackjack.ImageKey.PLAYER_CARDS;

public class TableDisplay extends Canvas {

    private final static int TEXT_OFFSET = 50;

    private final int DEALER_PLANE;
    private final int PLAYER_PLANE;

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

        context.setStroke(RED);

        HOR_CENTER = (int) getWidth() / 2;
        VER_CENTER = (int) getHeight() / 2;
        CARD_HEIGHT = (int) (screen.getHeight() * 0.175);
        CARD_WIDTH = (int) (screen.getWidth() * 0.08);
        GAP_BETWEEN_CARDS = (int) (screen.getWidth() * 0.15);
        BOTTOM = (int) getHeight();

        DEALER_PLANE = 100;
        PLAYER_PLANE = VER_CENTER + 110;
    }

    public void reset() {
        context.clearRect(0, 0, getWidth(), getHeight());
        context.setFill(valueOf("#228b22"));
        context.fillRect(0, 0, getWidth(), getHeight());
    }

    public void drawScores(int dealerScore, int playerScore) {
        drawLabel(String.format("Dealer: %s", dealerScore), DEALER_PLANE);
        drawLabel(String.format("You: %s", playerScore), PLAYER_PLANE);
    }

    public void drawCards(Map<ImageKey, List<Image>> imageMap) {
        drawLineOfCards(imageMap.get(DEALER_CARDS), DEALER_PLANE);
        drawLineOfCards(imageMap.get(PLAYER_CARDS), PLAYER_PLANE);
        context.strokeRect(0, 0, getWidth(), getHeight());
    }

    public void drawOpeningDealAnimation(Map<ImageKey, List<Image>> imageMap) {
        final Rectangle2D rect = new Rectangle2D(850, -100, 120, 150);

        final Point2D dealerDestination1 = new Point2D(750, 100);
        final Point2D dealerDestination2 = new Point2D(1100, 100);
        final Point2D playerDestination1 = new Point2D(750, 400);
        final Point2D playerDestination2 = new Point2D(1100, 400);

        final TravelingImage img1 = new TravelingImage(imageMap.get(PLAYER_CARDS).get(0), rect, playerDestination1,1);
        final TravelingImage img2 = new TravelingImage(imageMap.get(DEALER_CARDS).get(0), rect, dealerDestination1,1);
        final TravelingImage img3 = new TravelingImage(imageMap.get(PLAYER_CARDS).get(1), rect, playerDestination2,1);
        final TravelingImage img4 = new TravelingImage(imageMap.get(DEALER_CARDS).get(1), rect, dealerDestination2,1);

        final AnimationCallback callback3 = (existingImages) -> {
            final OpeningHandsAnimation animation = new OpeningHandsAnimation(context, img4, null, existingImages);

            animation.start();
        };

        final AnimationCallback callback2 = (existingImages) -> {
            final OpeningHandsAnimation animation = new OpeningHandsAnimation(context, img3, callback3, existingImages);

            animation.start();
        };

        final AnimationCallback callback1 = (existingImages) -> {
            final OpeningHandsAnimation animation = new OpeningHandsAnimation(context, img2, callback2, existingImages);

            animation.start();
        };

        final OpeningHandsAnimation animation = new OpeningHandsAnimation(context, img1, callback1, new ArrayList<>());

        animation.start();
    }

    public void drawHandsToPlay(List<List<Image>> cards) {
        for (int i = 0, y = BOTTOM - 100; i < cards.size(); i++, y -= 120) {
            drawSmallLineOfCards(cards.get(i), y);
        }
    }

    public void drawResults(String text, Color color) {
        final Font f = new Font("Arial", 50);
        context.setFont(f);
        context.setFill(color);
        context.fillText(text, HOR_CENTER - 50, VER_CENTER + 5);
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
