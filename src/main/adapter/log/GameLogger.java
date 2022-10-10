package main.adapter.log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Table;
import main.domain.model.Transaction;
import main.usecase.AccountRegistrar;
import main.usecase.TableObserver;

import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.logging.Level.INFO;
import static main.adapter.injection.Bindings.GAME_LOGGER;
import static main.adapter.injection.Bindings.LOG_HANDLERS;

public class GameLogger implements TableObserver, AccountRegistrar {

    private final Logger logger;

    @Inject
    public GameLogger(@Named(GAME_LOGGER) Logger logger, @Named(LOG_HANDLERS) Collection<Handler> logHandlers) {
        this.logger = logger;
        logger.setUseParentHandlers(false);
        logHandlers.forEach(logger::addHandler);
    }

    @Override
    public void onUpdate(Table table) {
        logger.log(INFO, format("%s: Round Snapshot%s",
                table.timestamp().toLocalTime(),
                table));
    }

    @Override
    public void createNew(Account account) {
        logger.log(INFO, format(
                "%s: Account Opened - %s [%s]",
                account.getTimestamp(),
                account.getName(),
                account.key()));
    }

    public void onTransactionsLoaded(Collection<Transaction> transactions) {
        logger.log(INFO, format("Loaded %s transactions.", transactions.size()));
    }

    public void onAccountsLoaded(Collection<Account> accounts) {
        logger.log(INFO, format("Loaded %s accounts.", accounts.size()));
    }

    public void onAccountDeleted(Account account) {
        logger.log(INFO, format("%s: Account Closure Request - %s [%s]",
                account.getTimestamp(),
                account.getName(),
                account.key()));
    }

    public void onTransaction(Transaction transaction) {
        logger.log(INFO, format("%s: Transaction Issued - Account Key: %s - %s - $%s",
                transaction.timestamp(),
                transaction.accountKey(),
                transaction.description(),
                Math.abs(transaction.amount())));
    }
}
