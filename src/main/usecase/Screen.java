package main.usecase;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

public enum Screen {
    BACK("NO PATH"),
    BET("../ui/bet/BetView.fxml"),
    GAME("../ui/blackjack/BlackjackView.fxml"),
    HOME("../ui/home/HomeView.fxml"),
    HISTORY("../ui/history/HistoryView.fxml"),
    REGISTRATION("../ui/registration/RegistrationView.fxml"),
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
