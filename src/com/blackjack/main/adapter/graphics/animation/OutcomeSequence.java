package com.blackjack.main.adapter.graphics.animation;

import com.blackjack.main.adapter.graphics.Vector;
import com.blackjack.main.domain.model.Outcome;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

import static com.blackjack.main.adapter.graphics.animation.TableDisplay.OUTCOME_COLORS;
import static com.blackjack.main.adapter.graphics.animation.TableDisplay.OUTCOME_STRINGS;


public class OutcomeSequence implements Callback {

    private final Outcome outcome;
    private final GraphicsContext graphics;
    private final Vector center;
    private final static String FONT_NAME = "Arial";


    private OutcomeSequence(
            GraphicsContext graphics,
            Outcome outcome,
            Vector center) {
        this.outcome = outcome;
        this.graphics = graphics;
        this.center = center;
    }

    public static OutcomeSequence outcomeSequence(
            GraphicsContext graphics,
            Outcome outcome,
            Vector vector) {
        return new OutcomeSequence(graphics, outcome, vector);
    }

    @Override
    public void call() {
        final Font f = new Font(FONT_NAME, 50);
        graphics.setFont(f);
        graphics.setFill(OUTCOME_COLORS.get(outcome));
        graphics.fillText(OUTCOME_STRINGS.get(outcome), center.x - 50, center.y + 50);
    }
}
