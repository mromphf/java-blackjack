package main.usecase;

import main.domain.Account;

public interface BalanceListener {
    void onBalanceUpdated(Account account);
}
