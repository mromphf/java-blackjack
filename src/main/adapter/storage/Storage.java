package main.adapter.storage;

import com.google.inject.Inject;
import main.domain.Account;
import main.domain.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.TransactionListener;

import java.util.Collection;


public class Storage extends EventConnection implements AccountListener, TransactionListener {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Inject
    public Storage(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public void loadAllAccounts() {
        eventNetwork.onAccountsLoaded(accountRepository.loadAllAccounts());
    }

    public void loadAllTransactions() {
        eventNetwork.onTransactionsLoaded(transactionRepository.loadAllTransactions());
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        transactionRepository.saveTransaction(transaction);
    }

    @Override
    public void onTransactionSeriesIssued(Collection<Transaction> transactions) {
        transactionRepository.saveTransactions(transactions);
    }

    @Override
    public void onAccountCreated(Account account) {
        accountRepository.openNewAccount(account);
    }

    @Override
    public void onAccountDeleted(Account account) {
        accountRepository.closeAccount(account);
    }
}
