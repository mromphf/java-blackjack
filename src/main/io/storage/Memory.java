package main.io.storage;

import main.domain.Account;
import main.domain.Card;
import main.domain.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public interface Memory {
    Stack<Card> loadDeck(String name);
    Set<Account> loadAllAccounts();
    List<Transaction> loadAllTransactions();
    void saveTransaction(Transaction transaction);
    void saveTransactions(Collection<Transaction> transactions);
    void openAccount(Account account);
    void closeAccount(Account account);
}
