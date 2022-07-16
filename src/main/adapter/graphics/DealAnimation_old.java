package main.adapter.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.concurrent.DelayQueue;

import static java.lang.System.exit;

public class DealAnimation_old implements Runnable {

    private final static int CODE_1 = 1;

    private final int HOR_CENTER;
    private final int CARD_WIDTH;
    private final int GAP_BETWEEN_CARDS;
    private final int CARD_HEIGHT;
    private final int VER_CENTER;

    private final GraphicsContext graphics;


    private final DelayQueue<DelayedElement<Image>> dealerCards;
    private final DelayQueue<DelayedElement<Image>> playerCards;

    public DealAnimation_old(
            int HOR_CENTER,
            int CARD_WIDTH,
            int GAP_BETWEEN_CARDS,
            int CARD_HEIGHT,
            int VER_CENTER,
            GraphicsContext graphics,
            DelayQueue<DelayedElement<Image>> dealerCards,
            DelayQueue<DelayedElement<Image>> playerCards) {
        this.dealerCards = dealerCards;
        this.playerCards = playerCards;
        this.HOR_CENTER = HOR_CENTER;
        this.CARD_HEIGHT = CARD_HEIGHT;
        this.CARD_WIDTH = CARD_WIDTH;
        this.GAP_BETWEEN_CARDS = GAP_BETWEEN_CARDS;
        this.VER_CENTER = VER_CENTER;
        this.graphics = graphics;
    }

    @Override
    public void run() {
        final int START_POS  = HOR_CENTER - (CARD_WIDTH * dealerCards.size() + (CARD_WIDTH / 2));

        for (int i = 0, x = START_POS; i < dealerCards.size(); i++, x += GAP_BETWEEN_CARDS) {
            try {
                Image image = dealerCards.take().data();
                graphics.drawImage(image, x, 100, CARD_WIDTH, CARD_HEIGHT);
            } catch(InterruptedException ex) {
                ex.printStackTrace();
                exit(CODE_1);
            }
        }

        for (int i = 0, x = START_POS; i < playerCards.size(); i++, x += GAP_BETWEEN_CARDS) {
            try {
                Image image = playerCards.take().data();
                graphics.drawImage(image, x, VER_CENTER + 110, CARD_WIDTH, CARD_HEIGHT);
            } catch(InterruptedException ex) {
                ex.printStackTrace();
                exit(CODE_1);
            }
        }
    }
}
