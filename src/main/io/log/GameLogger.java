package main.io.log;

import main.domain.Account;
import main.domain.Snapshot;
import main.usecase.GameStateListener;
import main.usecase.TransactionListener;

import java.time.LocalTime;
import java.util.logging.Logger;
import static java.util.logging.Level.*;

public class GameLogger extends Logger implements GameStateListener, TransactionListener {

    public GameLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        log(INFO, snapshot.toString());
    }

    @Override
    public void onBalanceChanged(Account account) {
        log(INFO, String.format("%s: Balance: %s", LocalTime.now(), account.getBalance()));
    }
}
