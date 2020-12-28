package main.io.history;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import main.domain.Account;
import main.domain.Transaction;
import main.io.RootController;
import main.usecase.MemoryListener;
import main.usecase.NavListener;
import main.usecase.TransactionListener;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static main.io.util.ChartUtil.balanceSeries;
import static main.io.util.ChartUtil.dateAxis;

public class HistoryController extends RootController implements Initializable, NavListener, MemoryListener, TransactionListener {

    @FXML
    public Label lblAccount;

    @FXML
    public Label lblTrans;

    @FXML
    public GridPane chartHousing;

    private List<Transaction> allTransactions = new LinkedList<>();

    @FXML
    public void onHome() {
        chartHousing.getChildren().clear();
        navListeners.forEach(NavListener::onStopPlaying);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onViewHistory(Account account) {
        final List<Transaction> accountTransactions = Transaction.listForAccount(account.getKey(), allTransactions);
        final Axis<String> xAxis = dateAxis(accountTransactions);
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setPrefWidth(1000);
        chart.setPrefHeight(800);
        chart.getData().add(balanceSeries(accountTransactions));

        chartHousing.add(chart, 0, 0);
        lblAccount.setText(account.getName());
        lblTrans.setText(String.format("Transactions: %s", accountTransactions.size()));
    }

    @Override
    public void onTransactionsLoaded(List<Transaction> transactions) {
        allTransactions = transactions;
    }

    @Override
    public void onTransaction(Transaction transaction) {
        allTransactions.add(transaction);
    }

    @Override
    public void onTransactions(List<Transaction> transactions) {
        allTransactions.addAll(transactions);
    }

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {}

    @Override
    public void onStartNewRound(int bet) {}

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onMoveToBettingTable(Account account) {}

    @Override
    public void onStopPlaying() {}
}
