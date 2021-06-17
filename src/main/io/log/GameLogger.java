package main.io.log;

import main.domain.Account;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.eventing.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static main.usecase.eventing.Predicate.*;

public class GameLogger extends Logger implements GameStateListener, BalanceListener, EventListener, AccountListener {

    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("kk:mm:ss");
    private EventNetwork eventNetwork;

    public GameLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }

    @Override
    public void onUpdate(Snapshot snapshot) {
        log(INFO, String.format("%s: SNAPSHOT - %s", snapshot.getTimestamp().toLocalTime().format(pattern), snapshot));
    }

    @Override
    public void onBalanceUpdated() {
        if (eventNetwork == null) {
            log(WARNING, "No network connection! Could not poll account balance.");
        } else {
            final Account account = eventNetwork.fulfill(CURRENT_BALANCE);
            log(INFO, String.format("%s: BALANCE - %s ($%s)", LocalTime.now().format(pattern), account.getName(), account.getBalance()));
        }
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(TRANSACTION)) {
            onTransaction(message.getTransaction());
        } else if (message.is(TRANSACTION_SERIES)) {
            message.getTransactions().forEach(this::onTransaction);
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
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        event.getData().forEach(account ->
                onAccountEvent(new Event<>(event.getPredicate(), account)));
    }

    public void onTransaction(Transaction transaction) {
        log(INFO, String.format("%s: TRANSACTION - %s ($%s) Account Key: %s",
                transaction.getTime().toLocalTime().format(pattern),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getAccountKey()));
    }
}
