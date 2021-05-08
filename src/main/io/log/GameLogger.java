package main.io.log;

import main.domain.Account;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.AccountListener;
import main.usecase.GameStateListener;
import main.usecase.BalanceListener;
import main.usecase.TransactionListener;

import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;
import static java.util.logging.Level.*;

public class GameLogger extends Logger implements GameStateListener, BalanceListener, AccountListener, TransactionListener {

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

    @Override
    public void onTransaction(Transaction transaction) {
        log(INFO, String.format("%s: %s (%s) Account Key: %s",
                transaction.getTime().toLocalTime(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getAccountKey()));
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        transactions.forEach(this::onTransaction);
    }
}
