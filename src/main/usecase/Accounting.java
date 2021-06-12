package main.usecase;

import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Accounting extends EventConnection implements TransactionListener, NavListener, MemoryListener {

    private static Account selectedAccount;
    private static final Collection<Transaction> transactions = new ArrayList<>();

    public Accounting() {
        selectedAccount = Account.placeholder();
    }

    @Override
    public void onTransactionsLoaded(List<Transaction> trans) {
        transactions.addAll(trans);
    }

    @Override
    public void onTransaction(Transaction transaction) {
        transactions.add(transaction);
        eventNetwork.onBalanceUpdated();
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        eventNetwork.onBalanceUpdated();
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        selectedAccount = account;
    }

    public static int currentBalance(UUID accountKey) {
        return transactions.stream()
                .filter(t -> t.getAccountKey().equals(accountKey))
                .mapToInt(Transaction::getAmount)
                .sum();
    }

    public static Account selectedAccount() {
        return selectedAccount;
    }

    public static UUID selectedAccountKey() {
        return selectedAccount.getKey();
    }

    @Override
    public void onChangeLayout(Layout layout) {}

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {}
}
