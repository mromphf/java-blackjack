package main.io.storage;

import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.*;

public interface TransactionMemory {
    Stack<Card> loadDeck(String name);
    Collection<Transaction> loadAllTransactions(Collection<Account> openAccoutns);
    Map<LocalDateTime, UUID> loadAllClosedAccountKeys();
    void saveTransaction(Transaction transaction);
    default void saveTransactions(Collection<Transaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }
}
