package com.blackjack.main.adapter.graphics.animation;

import com.blackjack.main.adapter.graphics.VectorFunctions;
import com.blackjack.main.adapter.ui.ImageService;
import com.blackjack.main.domain.model.Card;
import com.blackjack.main.domain.model.TableView;
import javafx.scene.canvas.GraphicsContext;

import java.util.stream.Stream;

import static com.blackjack.main.adapter.graphics.animation.DelayedSequence.delayedSequence;
import static com.blackjack.main.adapter.graphics.animation.ImageRow.imageRow;
import static com.blackjack.main.adapter.graphics.animation.OutcomeSequence.outcomeSequence;
import static com.blackjack.main.adapter.graphics.animation.RevealSequence.revealSequence;

public class AnimationFactory {

    private final GraphicsContext graphics;
    private final VectorFunctions vectorFunctions;
    private final ImageService images;

    public AnimationFactory(
            GraphicsContext graphics,
            TableDisplay tableDisplay,
            ImageService images) {
        this.graphics = graphics;
        this.images = images;

        this.vectorFunctions = new VectorFunctions(tableDisplay);
    }

    public DelayedSequence dealAnimation(Stream<Card> cards) {
        return delayedSequence(graphics,
                vectorFunctions.deal(),
                images.fromCards(cards));
    }

    public RevealSequence revealAnimation(TableView tableView) {
        final OutcomeSequence outcomeSequence = outcomeSequence(graphics,
                tableView.outcome(), vectorFunctions.center());

        return revealSequence(graphics,
                vectorFunctions.dealer(tableView.dealerHand().size()),
                images.fromCards(tableView.dealerHand().stream()),
                outcomeSequence);
    }

    public ImageRow playerImageRow(TableView tableView) {
        return imageRow(graphics,
                vectorFunctions.player(tableView.playerHand().size()),
                images.fromCards(tableView.playerHand().stream()));
    }

    public ImageRow dealerImageRow(TableView tableView) {
        return imageRow(graphics,
                vectorFunctions.dealer(tableView.dealerHand().size()),
                images.fromCards(tableView.dealerHand().stream()));
    }
}
