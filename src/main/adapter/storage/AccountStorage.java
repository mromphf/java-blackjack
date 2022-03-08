package main.adapter.storage;

import com.google.inject.Inject;
import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.ArrayList;
import java.util.Collection;


public class AccountStorage extends EventConnection implements AccountListener, TransactionListener {

    private final TransactionMemory transactionMemory;
    private final AccountMemory accountMemory;

    @Inject
    public AccountStorage(TransactionMemory transactionMemory, AccountMemory accountMemory) {
        this.transactionMemory = transactionMemory;
        this.accountMemory = accountMemory;
    }

    public void loadAllAccounts() {
        final Collection<Account> accounts = accountMemory.loadAllAccounts(new ArrayList<>());
        eventNetwork.onAccountsLoaded(accounts);
    }

    public void loadAllTransactions() {
        final Collection<Transaction> allTransactions = transactionMemory.loadAllTransactions(new ArrayList<>());
        eventNetwork.onTransactionsLoaded(allTransactions);
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        transactionMemory.saveTransaction(transaction);
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        transactionMemory.saveTransactions(transactions);
    }

    @Override
    public void onAccountCreated(Account account) {
        accountMemory.openNewAccount(account);
    }

    @Override
    public void onAccountDeleted(Account account) {
        accountMemory.closeAccount(account);
    }

    @Override
    public void onAccountSelected(Account account) {
        // No-op stub
    }

    @Override
    public void onAccountBalanceUpdated(Account account) {
        // No-op stub
    }
}
