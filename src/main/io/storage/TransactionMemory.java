package main.io.storage;

import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.util.Collection;
import java.util.Stack;

public interface TransactionMemory {
    Stack<Card> loadDeck(String name);
    Collection<Transaction> loadAllTransactions(Collection<Account> openAccoutns);
    void saveTransaction(Transaction transaction);
    default void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }
}
