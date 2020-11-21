package blackjack.io.eventHandling;

import blackjack.io.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class onHit implements EventHandler<ActionEvent> {

    private final Controller controller;

    public onHit(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent event) {
        controller.onHit();
    }
}
