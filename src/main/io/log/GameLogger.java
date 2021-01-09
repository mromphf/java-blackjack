package main.io.log;

import main.domain.Account;
import main.domain.Snapshot;
import main.usecase.*;

import java.time.LocalTime;
import java.util.logging.Logger;
import static java.util.logging.Level.*;
import static main.usecase.DataKey.*;
import static main.usecase.Predicate.BALANCE_UPDATED;

public class GameLogger extends Logger implements GameStateListener, EventListener {

    public GameLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        log(INFO, snapshot.toString());
    }

    @Override
    public void listen(Event e) {
        if (e.is(BALANCE_UPDATED)) {
            final Account account = (Account) e.getData(ACCOUNT);
            log(INFO, String.format("%s: %s's Balance: %s", LocalTime.now(), account.getName(), account.getBalance()));
        }
    }
}
