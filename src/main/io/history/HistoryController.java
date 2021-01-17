package main.io.history;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.GridPane;
import main.usecase.Layout;
import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.MemoryListener;
import main.usecase.NavListener;
import main.usecase.TransactionListener;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static main.usecase.Layout.*;
import static main.io.util.ChartUtil.balanceSeries;
import static main.io.util.ChartUtil.dateAxis;

public class HistoryController extends EventConnection implements Initializable, NavListener, MemoryListener, TransactionListener {

    @FXML
    public GridPane chartHousing;

    private List<Transaction> allTransactions = new LinkedList<>();

    @FXML
    public void onHome() {
        chartHousing.getChildren().clear();
        eventNetwork.onChangeLayout(HOME);
    }

    @FXML
    public void onDateSelected(ActionEvent event) {
        // TODO: how to get the date?
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        if (layout == HISTORY) {
            final List<Transaction> accountTransactions = Transaction.listForAccount(account.getKey(), allTransactions);
            final Axis<String> xAxis = dateAxis(accountTransactions);
            final NumberAxis yAxis = new NumberAxis();
            final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

            chart.setPrefWidth(1200);
            chart.setPrefHeight(800);
            chart.setTitle(String.format("%s Transactions: %s", account.getName(), accountTransactions.size()));
            chart.getData().add(balanceSeries(accountTransactions));

            chartHousing.add(chart, 0, 0);
        }
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
    public void onChangeLayout(Layout layout) {}

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {}
}
