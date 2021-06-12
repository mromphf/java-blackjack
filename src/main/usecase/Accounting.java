package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;

import java.util.List;

public class Accounting extends EventConnection implements TransactionListener, NavListener, AccountListener, Responder {

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
    public void onNewAccountOpened(Account account) {
        this.account = account;
    }

    @Override
    public void onChangeLayout(Layout layout) {}

    @Override
    public void onAccountDeleted(Account account) {}

    @Override
    public Response fulfill(NetworkElement elm) {
        return Response.of(account);
    }
}
