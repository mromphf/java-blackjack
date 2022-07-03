package main.adapter.ui;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

public enum Screen {
    BACK("NO PATH"),
    BET("Bet.fxml"),
    GAME("Blackjack.fxml"),
    HOME("Home.fxml"),
    HISTORY("History.fxml"),
    REGISTRATION("Registration.fxml"),
    ;

    private final String path;

    Screen(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public static Collection<Screen> screensWithPaths() {
        return of(BET, GAME, HOME, HISTORY, REGISTRATION).collect(toSet());
    }
}
