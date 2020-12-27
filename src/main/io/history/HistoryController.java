package main.io.history;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import main.domain.Account;
import main.domain.Transaction;
import main.io.RootController;
import main.io.util.ChartUtil;
import main.usecase.MemoryListener;
import main.usecase.NavListener;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryController extends RootController implements Initializable, NavListener, MemoryListener {

    @FXML
    public Label lblAccount;

    @FXML
    public Label lblTrans;

    @FXML
    public GridPane chartHousing;

    private List<Transaction> allTransactions = new LinkedList<>();

    @FXML
    public void onHome() {
        navListeners.forEach(NavListener::onStopPlaying);
    }

    @Override
    public void onViewHistory(Account account) {
        final List<Transaction> accountTransactions = allTransactions.stream()
                .filter(t -> t.getAccountKey().equals(account.getKey()))
                .collect(Collectors.toList());
        final Axis<String> xAxis = ChartUtil.dateAxis(accountTransactions);
        final NumberAxis yAxis = new NumberAxis(0, 500, 50);
        final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setPrefWidth(1000);
        chart.setPrefHeight(800);
        chart.getData().add(ChartUtil.balanceSeries(accountTransactions));

        chartHousing.add(chart, 0, 0);
        lblAccount.setText(account.getName());
        lblTrans.setText(String.format("Transactions: %s", accountTransactions.size()));
    }

    @Override
    public void onTransactionsLoaded(List<Transaction> transactions) {
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
