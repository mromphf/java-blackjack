package main.usecase;

import main.domain.Account;

public interface TransactionListener {
    void onAccountUpdated(Account account);
}
