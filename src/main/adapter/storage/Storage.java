package main.adapter.storage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.TransactionListener;

import java.util.Collection;

import static main.adapter.injection.Bindings.ACCOUNT_LISTENERS;
import static main.adapter.injection.Bindings.TRANSACTION_LISTENERS;


public class Storage implements AccountListener, TransactionListener {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final Collection<AccountListener> accountListeners;
    private final Collection<TransactionListener> transactionListeners;

    @Inject
    public Storage(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            @Named(TRANSACTION_LISTENERS) Collection<TransactionListener> transactionListeners,
            @Named(ACCOUNT_LISTENERS) Collection<AccountListener> accountListeners) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountListeners = accountListeners;
        this.transactionListeners = transactionListeners;
    }

    public void loadAllAccounts() {
        for (AccountListener accountListener : accountListeners) {
            accountListener.onAccountsLoaded(accountRepository.loadAllAccounts());
        }
    }

    public void loadAllTransactions() {
        for (TransactionListener transactionListener : transactionListeners) {
            transactionListener.onTransactionsLoaded(transactionRepository.loadAllTransactions());
        }
    }

    @Override
    public void onTransactionIssued(Transaction transaction) {
        transactionRepository.saveTransaction(transaction);
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
