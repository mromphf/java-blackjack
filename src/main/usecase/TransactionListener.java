package main.usecase;

import main.domain.Account;

public interface TransactionListener {
    void onBalanceChanged(Account account);
}
