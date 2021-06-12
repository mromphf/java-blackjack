package main.usecase;

import main.domain.Account;

public class Message {
    private final NetworkElement elm;
    private final Account account;

    private Message(NetworkElement elm, Account account) {
        this.elm = elm;
        this.account = account;
    }

    public static Message of(NetworkElement elm, Account account) {
        return new Message(elm, account);
    }

    public boolean is(NetworkElement elm) {
        return elm.equals(this.elm);
    }

    public int getCurrentBalance() {
        return account.getBalance();
    }

    public Account getAccount() {
        return account;
    }
}
