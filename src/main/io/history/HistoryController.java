package main.io.history;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.GridPane;
import main.usecase.*;
import main.domain.Account;
import main.domain.Transaction;
import main.io.EventConnection;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static main.usecase.DataKey.*;
import static main.usecase.Event.layoutChanged;
import static main.usecase.Layout.*;
import static main.io.util.ChartUtil.balanceSeries;
import static main.io.util.ChartUtil.dateAxis;
import static main.usecase.Predicate.*;

public class HistoryController extends EventConnection implements EventListener, Initializable {

    @FXML
    public GridPane chartHousing;

    private List<Transaction> allTransactions = new LinkedList<>();

    @FXML
    public void onHome() {
        chartHousing.getChildren().clear();
        eventNetwork.post(layoutChanged(HOME));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void listen(Event e) {
        if (e.is(ACCOUNT_SELECTED)) {
            final Account account = (Account) e.getData(ACCOUNT);
            drawChart(account);
        } else if (e.is(TRANSACTIONS_LOADED)) {
            allTransactions = e.getTransactions();
        } else if (e.is(Predicate.TRANSACTION)) {
            allTransactions.add(e.getTransaction());
        } else if (e.is(Predicate.TRANSACTIONS)) {
            allTransactions.addAll(e.getTransactions());
        }
    }

    public void drawChart(Account account) {
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
