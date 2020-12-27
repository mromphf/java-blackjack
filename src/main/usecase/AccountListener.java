package main.usecase;

import main.domain.Account;
import main.domain.Transaction;

import java.util.List;

public interface AccountListener extends TransactionListener {
    void onNewAccountOpened(Account account, int signingBonus);
    void onAccountDeleted(Account account);
    void onTransaction(Transaction transaction);
    void onTransactions(List<Transaction> transactions);
}
