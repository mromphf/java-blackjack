package main.usecase;

import main.domain.Account;

public class Response {
    private final Account selectedAccount;

    private Response(Account account) {
        this.selectedAccount = account;
    }

    public static Response of(Account account) {
        return new Response(account);
    }

    public int getCurrentBalance() {
        return selectedAccount.getBalance();
    }

    public Account getSelectedAccount() {
        return selectedAccount;
    }
}
