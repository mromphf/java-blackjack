package com.blackjack.main.util;

import javafx.collections.FXCollections;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import com.blackjack.main.domain.model.Transaction;

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
                .filter(t -> t.timestamp().getDayOfYear() == date.getDayOfYear())
                .collect(Collectors.toList()));
    }

    public static Axis<String> dateAxis(List<Transaction> transactions) {
        return new CategoryAxis(FXCollections.observableArrayList(transactions.stream()
                .filter(t -> Math.abs(t.amount()) > 0)
                .map(t -> t.timestamp().toString())
                .distinct()
                .sorted()
                .collect(Collectors.toList())));
    }

    public static Map<Transaction, XYChart.Data<String, Number>> transactionDataMap(List<Transaction> transactions) {
        return transactionDataMap(transactions, 0);
    }

    public static Map<Transaction, XYChart.Data<String, Number>> transactionDataMap(List<Transaction> transactions, LocalDate date) {
        final int startingBalance = transactions.stream()
                .filter(t -> t.timestamp().toLocalDate().isBefore(date))
                .map(Transaction::amount)
                .reduce(0, Integer::sum);

        return transactionDataMap(transactions.stream()
                .filter(t -> t.timestamp().toLocalDate().isEqual(date) ||
                        t.timestamp().toLocalDate().isAfter(date))
                .collect(Collectors.toList()), startingBalance);
    }

    public static Map<Transaction, XYChart.Data<String, Number>> transactionDataMap(List<Transaction> transactions, int balance) {
        final Map<Transaction, XYChart.Data<String, Number>> dataMap = new Hashtable<>();
        final Collection<Transaction> transactionsSortedFiltered = transactions.stream()
                .filter(t -> Math.abs(t.amount()) > 0)
                .sorted()
                .collect(Collectors.toList());

        for (Transaction t : transactionsSortedFiltered) {
            balance += t.amount();
            dataMap.put(t, new XYChart.Data<>(t.timestamp().toString(), balance));
        }

        return dataMap;
    }

    public static void installTransactionTooltips(Map<Transaction, XYChart.Data<String, Number>> transactionDataMap) {
        transactionDataMap.forEach((transaction, data) -> {
            final Tooltip tooltip = new Tooltip(format("%s: %s", transaction.description(), transaction.amount()));
            Tooltip.install(data.getNode(), tooltip);
        });
    }
}
