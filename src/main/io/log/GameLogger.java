package main.io.log;

import main.domain.Account;
import main.domain.Snapshot;
import main.usecase.AccountListener;
import main.usecase.GameStateListener;
import main.usecase.BalanceListener;

import java.time.LocalTime;
import java.util.logging.Logger;
import static java.util.logging.Level.*;

public class GameLogger extends Logger implements GameStateListener, BalanceListener, AccountListener {

    public GameLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        log(INFO, snapshot.toString());
    }

    @Override
    public void onBalanceUpdated(Account account) {
        log(INFO, String.format("%s: %s's Balance: %s", LocalTime.now(), account.getName(), account.getBalance()));
    }

    @Override
    public void onNewAccountOpened(Account account) {
        log(INFO, String.format(
                "%s: Account no. %s has been opened under the name %s.", LocalTime.now(), account.getKey(), account.getName()));
    }

    @Override
    public void onAccountDeleted(Account account) {
        log(INFO, String.format("%s: Request issued to close account no. %s.", LocalTime.now(), account.getKey()));
    }
}
