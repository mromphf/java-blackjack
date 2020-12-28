package main.usecase;

import main.domain.Transaction;

import java.util.List;

public interface TransactionListener {
    void onTransaction(Transaction transaction);
    void onTransactions(List<Transaction> transactions);
}
