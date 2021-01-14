package main.usecase;

import main.domain.Account;


public interface AccountListener {
    void onNewAccountOpened(Account account);
    void onAccountDeleted(Account account);
}
