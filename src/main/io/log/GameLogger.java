package main.io.log;

import main.domain.Account;
import main.domain.Action;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.eventing.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static main.usecase.eventing.Predicate.*;

public class GameLogger extends Logger implements SnapshotListener, AccountListener, TransactionListener, ActionListener {

    private final UUID key;
    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("kk:mm:ss");

    public GameLogger(UUID key, String name, String resourceBundleName) {
        super(name, resourceBundleName);
        this.key = key;
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        log(INFO, String.format("%s: SNAPSHOT - %s", snapshot.getTimestamp().toLocalTime().format(pattern), snapshot));
    }

    @Override
    public void onTransactionEvent(Event<Transaction> event) {
        if (event.is(TRANSACTION)) {
            onTransaction(event.getData());
        }
    }

    @Override
    public void onTransactionsEvent(Event<Collection<Transaction>> event) {
        if (event.is(TRANSACTION_SERIES)) {
            event.getData().forEach(this::onTransaction);
        }
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(ACCOUNT_CREATED)) {
            final Account account = event.getData();
            log(INFO, String.format(
                    "%s: Account no. %s has been opened under the name %s.", LocalTime.now().format(pattern), account.getKey(), account.getName()));
        } else if (event.is(ACCOUNT_DELETED)) {
            log(INFO, String.format("%s: Request issued to close account no. %s.", LocalTime.now().format(pattern), event.getData().getKey()));
        } else if (event.is(CURRENT_BALANCE)) {
            final Account account = event.getData();
            log(INFO, String.format("%s: BALANCE - %s ($%s)", LocalTime.now().format(pattern), account.getName(), account.getBalance()));
        }
    }

    @Override
    public void onActionEvent(Event<Action> event) {
        log(INFO, String.format("%s: ACTION EVENT - %s",
                event.getTimestamp().format(pattern),
                event.getData()));
    }

    public void onTransaction(Transaction transaction) {
        log(INFO, String.format("%s: TRANSACTION - %s ($%s) Account Key: %s",
                transaction.getTime().toLocalTime().format(pattern),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getAccountKey()));
    }
}
