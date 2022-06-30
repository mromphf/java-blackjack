package main.adapter.log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Snapshot;
import main.domain.model.Transaction;
import main.usecase.SnapshotListener;

import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.logging.Level.INFO;
import static main.adapter.injection.Bindings.GAME_LOGGER;
import static main.adapter.injection.Bindings.LOG_HANDLERS;

public class GameLogger implements SnapshotListener {

    private final Logger logger;

    @Inject
    public GameLogger(@Named(GAME_LOGGER) Logger logger, @Named(LOG_HANDLERS) Collection<Handler> logHandlers) {
        this.logger = logger;
        logger.setUseParentHandlers(false);
        logHandlers.forEach(logger::addHandler);
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        logger.log(INFO, format("%s: Round Snapshot%s",
                snapshot.getTimestamp().toLocalTime(),
                snapshot));
    }

    public void onTransactionIssued(Transaction transaction) {
        onTransaction(transaction);
    }

    public void onTransactionsLoaded(Collection<Transaction> transactions) {
        logger.log(INFO, format("Loaded %s transactions.", transactions.size()));
    }

    public void onAccountsLoaded(Collection<Account> accounts) {
        logger.log(INFO, format("Loaded %s accounts.", accounts.size()));
    }

    public void onAccountCreated(Account account) {
        logger.log(INFO, format(
                "%s: Account Opened - %s - %s.",
                account.getCreated(),
                account.getName(),
                account.getKey()));
    }

    public void onAccountDeleted(Account account) {
        logger.log(INFO, format("%s: Account Closure Request - %s - %s.",
                account.getCreated(),
                account.getName(),
                account.getKey()));
    }

    public void onTransaction(Transaction transaction) {
        logger.log(INFO, format("%s: Transaction Issued - Account Key: %s - %s - $%s",
                transaction.getTime(),
                transaction.getAccountKey(),
                transaction.getDescription(),
                Math.abs(transaction.getAmount())));
    }
}
