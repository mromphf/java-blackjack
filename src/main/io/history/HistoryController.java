package main.io.history;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import main.usecase.Layout;
import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;
import main.usecase.MemoryListener;
import main.usecase.NavListener;
import main.usecase.TransactionListener;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static main.domain.Transaction.*;
import static main.usecase.Layout.*;
import static main.io.util.ChartUtil.balanceSeries;
import static main.io.util.ChartUtil.dateAxis;

public class HistoryController extends EventConnection implements Initializable, NavListener, MemoryListener, TransactionListener {

    @FXML
    public DatePicker datePicker;

    @FXML
    public GridPane chartHousing;

    private List<Transaction> allTransactions = new LinkedList<>();
    private Account account;

    @FXML
    public void onBack() {
        chartHousing.getChildren().clear();
        datePicker.setValue(null);
        eventNetwork.onChangeLayout(BACK);
    }

    @FXML
    public void onDateSelected() {
        final List<Transaction> accountTransactions = listForAccount(account.getKey(), allTransactions);
        final LocalDate date = datePicker.getValue();
        final NumberAxis yAxis = new NumberAxis();
        final Axis<String> xAxis = date == null ? dateAxis(accountTransactions) : dateAxis(accountTransactions, date);
        final XYChart.Series<String, Number> balanceSeries =
                date == null ? balanceSeries(accountTransactions) : balanceSeries(accountTransactions, date);

        chartHousing.getChildren().clear();

        drawChart(account, xAxis, yAxis, balanceSeries);
    }

    @FXML
    public void clearFilter() {
        final List<Transaction> accountTransactions = listForAccount(account.getKey(), allTransactions);
        final NumberAxis yAxis = new NumberAxis();
        final Axis<String> xAxis = dateAxis(accountTransactions);
        final XYChart.Series<String, Number> balanceSeries = balanceSeries(accountTransactions);

        datePicker.setValue(null);

        drawChart(account, xAxis, yAxis, balanceSeries);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        if (layout == HISTORY) {
            this.account = account;
            final List<Transaction> accountTransactions = listForAccount(account.getKey(), allTransactions);
            drawChart(
                    account,
                    dateAxis(listForAccount(account.getKey(), allTransactions)),
                    new NumberAxis(),
                    balanceSeries(accountTransactions)
            );
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

    public void drawChart(Account account, Axis<String> xAxis, NumberAxis yAxis, XYChart.Series<String, Number> series) {
        final List<Transaction> accountTransactions = listForAccount(account.getKey(), allTransactions);
        final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setPrefWidth(1200);
        chart.setPrefHeight(800);
        chart.setTitle(String.format("%s Transactions: %s", account.getName(), accountTransactions.size()));
        chart.getData().add(series);

        chartHousing.add(chart, 0, 0);
    }
}
