package main.io.log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.Account;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.SnapshotListener;
import main.usecase.eventing.TransactionListener;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static main.usecase.eventing.Predicate.*;

public class GameLogger extends Logger implements SnapshotListener, AccountListener, TransactionListener {

    private final UUID key;
    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("kk:mm:ss");

    @Inject
    public GameLogger(@Named("random") UUID key, @Named("logger") String name) {
        super(name, null);
        this.key = key;
        addHandler(new ConsoleLogHandler());
        addHandler(new FileLogHandler());
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        log(INFO, String.format("%s: Round Snapshot%s",
                snapshot.getTimestamp().toLocalTime().format(pattern),
                snapshot));
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
                    "%s: Account Opened - %s - %s.",
                    event.getTimestamp().format(pattern),
                    account.getName(),
                    account.getKey()));
        } else if (event.is(ACCOUNT_DELETED)) {
            log(INFO, String.format("%s: Account Closure Request - %s - %s.",
                    event.getTimestamp().format(pattern),
                    event.getData().getName(),
                    event.getData().getKey()));
        } else if (event.is(CURRENT_BALANCE)) {
            final Account account = event.getData();
            log(INFO, String.format("%s: Current Balance - %s - $%s",
                    event.getTimestamp().format(pattern),
                    account.getName(),
                    account.getBalance()));
        }
    }

    public void onTransaction(Transaction transaction) {
        log(INFO, String.format("%s: Transaction Issued - Account Key: %s - %s - $%s",
                transaction.getTime().toLocalTime().format(pattern),
                transaction.getAccountKey(),
                transaction.getDescription(),
                Math.abs(transaction.getAmount())));
    }
}
