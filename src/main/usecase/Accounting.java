package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;

import java.util.List;

import static main.usecase.NetworkElement.ACCOUNT_CREATED;

public class Accounting extends EventConnection implements TransactionListener, NavListener, Responder, EventListener {

    private Account account;

    public Accounting() {
        this.account = Account.placeholder();
    }

    @Override
    public void onTransaction(Transaction transaction) {
        this.account = account.updateBalance(transaction);
        eventNetwork.onBalanceUpdated();
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        this.account = account.updateBalance(transactions);
        eventNetwork.onBalanceUpdated();
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        this.account = account;
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(ACCOUNT_CREATED)) {
            this.account = message.getAccount();
        }
    }

    @Override
    public void onChangeLayout(Layout layout) {}

    @Override
    public Message fulfill(NetworkElement elm) {
        return Message.of(elm, account);
    }
}
