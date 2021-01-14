package main.io.storage;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.AccountListener;
import main.usecase.TransactionListener;

import java.time.LocalDateTime;
import java.util.*;

public class AccountStorage extends EventConnection implements AccountListener, TransactionListener {

    private final Memory memory;

    public AccountStorage(Memory memory) {
        this.memory = memory;
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = memory.loadAllAccounts();
        eventNetwork.onAccountsLoaded(accounts);
    }

    public void loadAllTransactions() {
        final List<Transaction> transactions = memory.loadAllTransactions();
        eventNetwork.onTransactionsLoaded(transactions);
    }

    @Override
    public void onNewAccountOpened(Account account) {
        memory.saveNewAccount(account);
    }

    @Override
    public void onAccountDeleted(Account account) {
        memory.deleteAccount(account);
    }

    @Override
    public void onTransaction(Transaction transaction) {
        memory.saveTransaction(transaction);
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        memory.saveTransactions(transactions);
    }
}
