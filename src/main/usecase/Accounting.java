package main.usecase;

import main.domain.Account;
import main.io.EventConnection;

import static main.usecase.NetworkElement.*;

public class Accounting extends EventConnection implements NavListener, Responder, EventListener {

    private Account account;

    public Accounting() {
        this.account = Account.placeholder();
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        this.account = account;
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(ACCOUNT_CREATED)) {
            this.account = message.getAccount();
        } else if (message.is(TRANSACTION)) {
            this.account = account.updateBalance(message.getTransaction());
            eventNetwork.onBalanceUpdated();
        } else if (message.is(TRANSACTION_SERIES)) {
            this.account = account.updateBalance(message.getTransactions());
            eventNetwork.onBalanceUpdated();
        }
    }

    @Override
    public void onChangeLayout(Layout layout) {}

    @Override
    public Message fulfill(NetworkElement elm) {
        return Message.of(elm, account);
    }
}
