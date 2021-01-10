package main.io.log;

import main.domain.Account;
import main.usecase.Event;
import main.usecase.EventListener;
import main.usecase.EventNetwork;

import java.time.LocalTime;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static main.usecase.Predicate.BALANCE_UPDATED;
import static main.usecase.Predicate.GAME_STATE_CHANGED;

public class GameLogger extends Logger implements EventListener {

    public GameLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void listen(Event e) {
        if (e.is(BALANCE_UPDATED)) {
            final Account account = e.getAccount();
            log(INFO, String.format("%s: %s's Balance: %s", LocalTime.now(), account.getName(), account.getBalance()));
        } else if (e.is(GAME_STATE_CHANGED)) {
            log(INFO, e.getSnapshot().toString());
        }
    }

    @Override
    public void connectTo(EventNetwork eventNetwork) {}
}
