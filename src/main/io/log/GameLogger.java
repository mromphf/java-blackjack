package main.io.log;

import main.domain.Account;
import main.domain.Snapshot;
import main.usecase.*;

import java.time.LocalTime;
import java.util.logging.Logger;
import static java.util.logging.Level.*;
import static main.usecase.DataKey.*;
import static main.usecase.Predicate.BALANCE_UPDATED;
import static main.usecase.Predicate.GAME_STATE_CHANGED;

public class GameLogger extends Logger implements EventListener {

    public GameLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void listen(Event e) {
        if (e.is(BALANCE_UPDATED)) {
            final Account account = (Account) e.getData(ACCOUNT);
            log(INFO, String.format("%s: %s's Balance: %s", LocalTime.now(), account.getName(), account.getBalance()));
        } else if (e.is(GAME_STATE_CHANGED)) {
            final Snapshot snapshot = (Snapshot) e.getData(SNAPSHOT);
            log(INFO, snapshot.toString());
        }
    }
}
