package main.io.history;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import main.domain.Account;
import main.domain.Transaction;
import main.usecase.Layout;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static main.common.ChartUtil.*;
import static main.usecase.Layout.BACK;
import static main.usecase.Layout.HISTORY;
import static main.usecase.eventing.Predicate.ACCOUNT_SELECTED;
import static main.usecase.eventing.Predicate.LAYOUT_CHANGED;

public class HistoryController extends EventConnection implements Initializable, LayoutListener {

    @FXML
    public DatePicker datePicker;

    @FXML
    public GridPane chartHousing;

    private final UUID key = randomUUID();

    @FXML
    public void onBack() {
        chartHousing.getChildren().clear();
        datePicker.setValue(null);
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, BACK));
    }

    @FXML
    public void onDateSelected() {
        final Optional<Account> account = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED);

        if (account.isPresent()) {
            final List<Transaction> accountTransactions = new ArrayList<>(
                    eventNetwork.requestTransactionsByKey(account.get().getKey()));
            final LocalDate date = datePicker.getValue();
            final NumberAxis yAxis = new NumberAxis();
            final Axis<String> xAxis = date == null ? dateAxis(accountTransactions) : dateAxis(accountTransactions, date);
            final Map<Transaction, XYChart.Data<String, Number>> balanceSeries =
                    date == null ? transactionDataMap(accountTransactions) : transactionDataMap(accountTransactions, date);

            chartHousing.getChildren().clear();

            drawChart(account.get(), xAxis, yAxis, balanceSeries);
        }
    }

    @FXML
    public void clearFilter() {
        final Optional<Account> selectedAccount = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED);
        if (selectedAccount.isPresent()) {
            final Account account = selectedAccount.get();
            final List<Transaction> accountTransactions = new ArrayList<>(
                    eventNetwork.requestTransactionsByKey(account.getKey()));
            final NumberAxis yAxis = new NumberAxis();
            final Axis<String> xAxis = dateAxis(accountTransactions);

            datePicker.setValue(null);

            drawChart(account, xAxis, yAxis, transactionDataMap(accountTransactions));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        final Optional<Account> selectedAccount = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED);

        if (event.is(LAYOUT_CHANGED) && event.getData() == HISTORY && selectedAccount.isPresent()) {
            final Account account = selectedAccount.get();
            final List<Transaction> transactions = new ArrayList<>(eventNetwork.requestTransactionsByKey(account.getKey()));

            drawChart(account, dateAxis(transactions), new NumberAxis(), transactionDataMap(transactions));
        }
    }

    public void drawChart(Account account,
                          Axis<String> xAxis,
                          NumberAxis yAxis,
                          Map<Transaction, XYChart.Data<String, Number>> transactionDataMap) {
        final List<Transaction> transactions = new ArrayList<>(eventNetwork.requestTransactionsByKey(account.getKey()));
        final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        final XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.setName("Transactions");

        for (XYChart.Data<String, Number> dataPoint : transactionDataMap.values()) {
            series.getData().add(dataPoint);
        }

        chart.setPrefWidth(1200);
        chart.setPrefHeight(800);
        chart.setTitle(String.format("%s Transactions: %s", account.getName(), transactions.size()));
        chart.getData().add(series);

        installTransactionTooltips(transactionDataMap);
        Platform.runLater(() -> chartHousing.add(chart, 0, 0));
    }
}
