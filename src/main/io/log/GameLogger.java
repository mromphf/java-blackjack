package main.io.log;

import main.domain.Account;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import static java.util.logging.Level.*;

public class GameLogger extends Logger implements GameStateListener, BalanceListener, AccountListener, TransactionListener {

    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("kk:mm:ss");

    public GameLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        log(INFO, String.format("%s: SNAPSHOT - %s", snapshot.getTimestamp().toLocalTime().format(pattern), snapshot));
    }

    @Override
    public void onBalanceUpdated() {
        final Account selectedAccount = Accounting.selectedAccount();
        log(INFO, String.format("%s: BALANCE - %s ($%s)",
                LocalTime.now().format(pattern), selectedAccount.getName(), selectedAccount.getBalance()));
    }

    @Override
    public void onNewAccountOpened(Account account) {
        log(INFO, String.format(
                "%s: Account no. %s has been opened under the name %s.", LocalTime.now().format(pattern), account.getKey(), account.getName()));
    }

    @Override
    public void onAccountDeleted(Account account) {
        log(INFO, String.format("%s: Request issued to close account no. %s.", LocalTime.now().format(pattern), account.getKey()));
    }

    @Override
    public void onTransaction(Transaction transaction) {
        log(INFO, String.format("%s: TRANSACTION - %s ($%s) Account Key: %s",
                transaction.getTime().toLocalTime().format(pattern),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getAccountKey()));
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        transactions.forEach(this::onTransaction);
    }
}
