package main.io.storage;

import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.*;

import java.util.List;

import static main.usecase.NetworkElement.*;


public class AccountStorage extends EventConnection implements TransactionListener, EventListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Message message = Message.of(ACCOUNTS_LOADED, memory.loadAllAccounts());
        eventNetwork.onEvent(message);
    }

    public void loadAllTransactions() {
        final Message message = Message.of(TRANSACTIONS_LOADED, memory.loadAllTransactions());
        eventNetwork.onEvent(message);
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
