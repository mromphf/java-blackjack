package main.io.history;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.domain.Account;
import main.domain.Transaction;
import main.io.RootController;
import main.usecase.MemoryListener;
import main.usecase.NavListener;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class HistoryController extends RootController implements Initializable, NavListener, MemoryListener {

    @FXML
    public Label lblAccount;

    @FXML
    public Label lblTrans;

    private Set<Transaction> allTransactions = new HashSet<>();

    @FXML
    public void onHome() {
        navListeners.forEach(NavListener::onStopPlaying);
    }

    @Override
    public void onViewHistory(Account account) {
        lblAccount.setText(account.getName());
        Set<Transaction> accountTransactions = allTransactions.stream()
                .filter(t -> t.getAccountKey().equals(account.getKey()))
                .collect(Collectors.toSet());
        lblTrans.setText(String.format("Transactions: %s", accountTransactions.size()));
    }

    @Override
    public void onTransactionsLoaded(Set<Transaction> transactions) {
        allTransactions = transactions;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onStartNewRound(int bet) {}

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onMoveToBettingTable(Account account) {}

    @Override
    public void onStopPlaying() {}

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {}
}
