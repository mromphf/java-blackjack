package main.usecase;

import main.domain.Account;
import main.io.EventConnection;

import static main.usecase.Predicate.*;

public class Accounting extends EventConnection implements Responder, EventListener {

    private Account account;

    public Accounting() {
        this.account = null;
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(ACCOUNT_CREATED) || message.is(ACCOUNT_SELECTED)) {
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
    public Message fulfill(Predicate predicate) {
        return Message.of(predicate, account);
    }
}
