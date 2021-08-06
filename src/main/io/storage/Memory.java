package main.io.storage;

import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.*;

public interface Memory {
    Stack<Card> loadDeck(String name);
    Set<Account> loadAllAccounts(Collection<UUID> closedKeys);
    List<Transaction> loadAllTransactions(Collection<UUID> closedKeys);
    Map<LocalDateTime, UUID> loadAllClosedAccountKeys();
    void saveTransaction(Transaction transaction);
    void saveTransactions(Collection<Transaction> transactions);
    void openNewAccount(Account account);
    void closeAccount(Account account);
}
