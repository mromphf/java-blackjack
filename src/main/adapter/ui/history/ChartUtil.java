package main.adapter.ui.history;

import javafx.collections.FXCollections;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import main.domain.model.Transaction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ChartUtil {

    public static Axis<String> dateAxis(List<Transaction> transactions, LocalDate date) {
        return dateAxis(transactions.stream()
                .filter(t -> t.getTime().getDayOfYear() == date.getDayOfYear())
                .collect(Collectors.toList()));
    }

    public static Axis<String> dateAxis(List<Transaction> transactions) {
        return new CategoryAxis(FXCollections.observableArrayList(transactions.stream()
                .filter(t -> Math.abs(t.getAmount()) > 0)
                .map(t -> t.getTime().toString())
                .distinct()
                .sorted()
                .collect(Collectors.toList())));
    }

    public static Map<Transaction, XYChart.Data<String, Number>> transactionDataMap(List<Transaction> transactions) {
        return transactionDataMap(transactions, 0);
    }

    public static Map<Transaction, XYChart.Data<String, Number>> transactionDataMap(List<Transaction> transactions, LocalDate date) {
        final int startingBalance = transactions.stream()
                .filter(t -> t.getTime().toLocalDate().isBefore(date))
                .map(Transaction::getAmount)
                .reduce(0, Integer::sum);

        return transactionDataMap(transactions.stream()
                .filter(t -> t.getTime().toLocalDate().isEqual(date) ||
                        t.getTime().toLocalDate().isAfter(date))
                .collect(Collectors.toList()), startingBalance);
    }

    public static Map<Transaction, XYChart.Data<String, Number>> transactionDataMap(List<Transaction> transactions, int balance) {
        final Map<Transaction, XYChart.Data<String, Number>> dataMap = new Hashtable<>();
        final Collection<Transaction> transactionsSortedFiltered = transactions.stream()
                .filter(t -> Math.abs(t.getAmount()) > 0)
                .sorted()
                .collect(Collectors.toList());

        for (Transaction t : transactionsSortedFiltered) {
            balance += t.getAmount();
            dataMap.put(t, new XYChart.Data<>(t.getTime().toString(), balance));
        }

        return dataMap;
    }

    public static void installTransactionTooltips(Map<Transaction, XYChart.Data<String, Number>> transactionDataMap) {
        transactionDataMap.forEach((transaction, data) -> {
            final Tooltip tooltip = new Tooltip(format("%s: %s", transaction.getDescription(), transaction.getAmount()));
            Tooltip.install(data.getNode(), tooltip);
        });
    }
}
