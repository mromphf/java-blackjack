package main.usecase;

import main.domain.Account;


public interface AccountListener {
    void onNewAccountOpened(Account account, int signingBonus);
    void onAccountDeleted(Account account);
}
