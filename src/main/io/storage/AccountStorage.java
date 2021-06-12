package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.*;

import java.util.Collection;
import java.util.List;

import static main.usecase.NetworkElement.ACCOUNT_CREATED;
import static main.usecase.NetworkElement.ACCOUNT_DELETED;


public class AccountStorage extends EventConnection implements TransactionListener, EventListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = memory.loadAllAccounts();
        eventNetwork.onAccountsLoaded(accounts);
    }

    public void loadAllTransactions() {
        final List<Transaction> transactions = memory.loadAllTransactions();
        eventNetwork.onTransactionsLoaded(transactions);
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(ACCOUNT_CREATED)) {
            memory.saveNewAccount(message.getAccount());
        } else if (message.is(ACCOUNT_DELETED)){
            memory.deleteAccount(message.getAccount());
        }
    }

    @Override
    public void onTransaction(Transaction transaction) {
        memory.saveTransaction(transaction);
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        memory.saveTransactions(transactions);
    }
}
