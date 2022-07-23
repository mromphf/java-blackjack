package main.adapter.ui;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.AccountStore;
import main.usecase.TransactionStore;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static main.adapter.ui.Screen.BACK;
import static main.util.ChartUtil.*;

public class HistoryController implements Initializable, ScreenObserver {

    @FXML
    public DatePicker datePicker;

    @FXML
    public GridPane chartHousing;

    private final TransactionStore transactionStore;
    private final AccountStore accountStore;
    private final ScreenManagement screen;

    @Inject
    public HistoryController(TransactionStore transactionStore,
                             AccountStore accountStore,
                             ScreenManagement screen) {
        this.transactionStore = transactionStore;
        this.accountStore = accountStore;
        this.screen = screen;
    }

    @FXML
    public void onBack() {
        chartHousing.getChildren().clear();
        datePicker.setValue(null);
        screen.switchTo(BACK);
    }

    @FXML
    public void onDateSelected() {
        final Optional<Account> optionalAccount = accountStore.selectedAccount();
        if (optionalAccount.isPresent()) {
            final Account selectedAccount = optionalAccount.get();
            final List<Transaction> accountTransactions = transactionStore.getTransactionsByKey(selectedAccount.getKey());
            final LocalDate date = datePicker.getValue();
            final NumberAxis yAxis = new NumberAxis();
            final Axis<String> xAxis = date == null ? dateAxis(accountTransactions) : dateAxis(accountTransactions, date);
            final Map<Transaction, XYChart.Data<String, Number>> balanceSeries =
                    date == null ? transactionDataMap(accountTransactions) : transactionDataMap(accountTransactions, date);

            chartHousing.getChildren().clear();

            drawChart(selectedAccount, xAxis, yAxis, balanceSeries);
        }
    }

    @FXML
    public void clearFilter() {
        final Optional<Account> optionalAccount = accountStore.selectedAccount();

        if (optionalAccount.isPresent()) {

            final Account selectedAccount = optionalAccount.get();
            final List<Transaction> accountTransactions = transactionStore.getTransactionsByKey(selectedAccount.getKey());
            final NumberAxis yAxis = new NumberAxis();
            final Axis<String> xAxis = dateAxis(accountTransactions);

            datePicker.setValue(null);

            drawChart(selectedAccount, xAxis, yAxis, transactionDataMap(accountTransactions));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onScreenChanged() {
        final Optional<Account> optionalAccount = accountStore.selectedAccount();

        if (optionalAccount.isPresent()) {
            final Account selectedAccount = optionalAccount.get();
            final List<Transaction> transactions = transactionStore.getTransactionsByKey(selectedAccount.getKey());

            drawChart(selectedAccount, dateAxis(transactions), new NumberAxis(), transactionDataMap(transactions));
        }
    }

    public void drawChart(Account account,
                          Axis<String> xAxis,
                          NumberAxis yAxis,
                          Map<Transaction, XYChart.Data<String, Number>> transactionDataMap) {
        final LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        final XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.setName("Transactions");

        for (XYChart.Data<String, Number> dataPoint : transactionDataMap.values()) {
            series.getData().add(dataPoint);
        }

        chart.setPrefWidth(1200);
        chart.setPrefHeight(800);
        chart.setTitle(String.format("%s Transactions: %s", account.getName(), 0));
        chart.getData().add(series);

        installTransactionTooltips(transactionDataMap);
        Platform.runLater(() -> chartHousing.add(chart, 0, 0));
    }
}
