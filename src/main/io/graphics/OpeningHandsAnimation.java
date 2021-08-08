package main.io.graphics;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.io.blackjack.ImageKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OpeningHandsAnimation extends AnimationTimer {

    private final GraphicsContext graphics;
    private final Collection<TravelingImage> travelingImages = new ArrayList<>();

    public OpeningHandsAnimation(GraphicsContext graphics, Map<ImageKey, List<Image>> imageMap) {
        this.graphics = graphics;

        final Image dealerCard1 = imageMap.get(ImageKey.DEALER_CARDS).get(0);
        final Image dealerCard2 = imageMap.get(ImageKey.DEALER_CARDS).get(1);
        final Image playerCard1 = imageMap.get(ImageKey.PLAYER_CARDS).get(0);
        final Image playerCard2 = imageMap.get(ImageKey.PLAYER_CARDS).get(1);

        final Point2D dealerDestination1 = new Point2D(750, 100);
        final Point2D dealerDestination2 = new Point2D(1100, 100);
        final Point2D playerDestination1 = new Point2D(750, 400);
        final Point2D playerDestination2 = new Point2D(1100, 400);

        final Rectangle2D rect = new Rectangle2D(850, -250, 120, 150);

        travelingImages.add(new TravelingImage(dealerCard1, rect, dealerDestination1, 1));
        travelingImages.add(new TravelingImage(dealerCard2, rect, dealerDestination2, 1));
        travelingImages.add(new TravelingImage(playerCard1, rect, playerDestination1, 1));
        travelingImages.add(new TravelingImage(playerCard2, rect, playerDestination2, 1));
    }

    @Override
    public void handle(long now) {
        graphics.clearRect(0, 0, 2000, 2000);
        travelingImages.forEach(TravelingImage::move);
        travelingImages.forEach(img -> img.draw(graphics));

        boolean allImagesReachedDestination = false;

        for (TravelingImage img : travelingImages) {
            allImagesReachedDestination = img.hasReachedDestination();
        }

        if (allImagesReachedDestination)
            stop();
    }
}
