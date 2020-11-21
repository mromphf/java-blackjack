package blackjack.io.eventHandling;

import blackjack.io.Screen;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class onStand implements EventHandler<ActionEvent> {

    private final Screen screen;

    public onStand(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void handle(ActionEvent event) {
        screen.blackScreen();
    }
}
