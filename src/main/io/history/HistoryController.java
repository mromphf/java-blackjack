package main.io.history;

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
import main.usecase.eventing.EventConnection;
import main.usecase.*;
import main.usecase.eventing.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static java.time.LocalDateTime.*;
import static java.util.UUID.*;
import static main.common.ChartUtil.balanceSeries;
import static main.common.ChartUtil.dateAxis;
import static main.usecase.Layout.BACK;
import static main.usecase.Layout.HISTORY;
import static main.usecase.eventing.Predicate.*;

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
        final Account account = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED);
        final List<Transaction> accountTransactions = new ArrayList<>(
                eventNetwork.requestTransactionsByKey(account.getKey()));
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
        final Account account = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED);
        final List<Transaction> accountTransactions = new ArrayList<>(
                eventNetwork.requestTransactionsByKey(account.getKey()));
        final NumberAxis yAxis = new NumberAxis();
        final Axis<String> xAxis = dateAxis(accountTransactions);
        final XYChart.Series<String, Number> balanceSeries = balanceSeries(accountTransactions);

        datePicker.setValue(null);

        drawChart(account, xAxis, yAxis, balanceSeries);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        if (event.is(LAYOUT_CHANGED) && event.getData() == HISTORY) {
            final Account account = eventNetwork.requestSelectedAccount(ACCOUNT_SELECTED);
            final List<Transaction> transactions = new ArrayList<>(eventNetwork.requestTransactionsByKey(account.getKey()));

            drawChart(account, dateAxis(transactions), new NumberAxis(), balanceSeries(transactions));
        }
    }

    public void drawChart(Account account, Axis<String> xAxis, NumberAxis yAxis, XYChart.Series<String, Number> series) {
        final List<Transaction> transactions = new ArrayList<>(eventNetwork.requestTransactionsByKey(account.getKey()));
        final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setPrefWidth(1200);
        chart.setPrefHeight(800);
        chart.setTitle(String.format("%s Transactions: %s", account.getName(), transactions.size()));
        chart.getData().add(series);

        chartHousing.add(chart, 0, 0);
    }
}
