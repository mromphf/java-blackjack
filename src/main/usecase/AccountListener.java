package main.usecase;

import main.domain.Account;

public interface AccountListener extends TransactionListener {
    void onNewAccountOpened(Account account);
}
