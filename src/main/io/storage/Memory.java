package main.io.storage;

import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.time.LocalDateTime;
import java.util.*;

public interface Memory {
    Stack<Card> loadDeck(String name);
    Collection<Account> loadAllAccounts(Collection<UUID> closedKeys);
    Collection<Transaction> loadAllTransactions(Collection<Account> openAccoutns);
    Map<LocalDateTime, UUID> loadAllClosedAccountKeys();
    void saveTransaction(Transaction transaction);
    void saveTransactions(Collection<Transaction> transactions);
    void openNewAccount(Account account);
    void closeAccount(Account account);
}
