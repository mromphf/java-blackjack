package main.adapter.log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.Account;
import main.domain.Snapshot;
import main.domain.Transaction;
import main.usecase.eventing.*;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static java.util.UUID.randomUUID;
import static java.util.logging.Level.INFO;
import static main.usecase.eventing.Predicate.*;

public class GameLogger extends EventConnection implements SnapshotListener, AccountListener, TransactionListener {

    private final UUID key = randomUUID();
    private final Logger logger;
    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("kk:mm:ss");

    @Inject
    public GameLogger(@Named("logger") Logger logger, @Named("logHandlers") Collection<Handler> logHandlers) {
        this.logger = logger;
        logHandlers.forEach(logger::addHandler);
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        logger.log(INFO, String.format("%s: Round Snapshot%s",
                snapshot.getTimestamp().toLocalTime().format(pattern),
                snapshot));
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        onTransaction(transaction);
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        transactions.forEach(this::onTransaction);
    }

    @Override
    public void onAccountCreated(Account account) {
        logger.log(INFO, String.format(
                "%s: Account Opened - %s - %s.",
                account.getCreated().format(pattern),
                account.getName(),
                account.getKey()));
    }

    @Override
    public void onAccountDeleted(Account account) {
        logger.log(INFO, String.format("%s: Account Closure Request - %s - %s.",
                account.getCreated().format(pattern),
                account.getName(),
                account.getKey()));
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(CURRENT_BALANCE_UPDATED)) {
            final Account account = event.getData();
            logger.log(INFO, String.format("%s: Current Balance - %s - $%s",
                    event.getTimestamp().format(pattern),
                    account.getName(),
                    account.getBalance()));
        }
    }

    public void onTransaction(Transaction transaction) {
        logger.log(INFO, String.format("%s: Transaction Issued - Account Key: %s - %s - $%s",
                transaction.getTime().toLocalTime().format(pattern),
                transaction.getAccountKey(),
                transaction.getDescription(),
                Math.abs(transaction.getAmount())));
    }
}
