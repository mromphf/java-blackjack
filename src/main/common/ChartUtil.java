package main.common;

import javafx.collections.FXCollections;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import main.domain.Transaction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public static XYChart.Series<String, Number> balanceSeries(List<Transaction> transactions, LocalDate date) {
        return balanceSeries(transactions.stream()
                .filter(t -> t.getTime().getDayOfYear() == date.getDayOfYear())
                .collect(Collectors.toList()));
    }

    public static XYChart.Series<String, Number> balanceSeries(List<Transaction> transactions) {
        final XYChart.Series<String, Number> series = new XYChart.Series<>();
        final Collection<Transaction> transactionsSortedFiltered = transactions.stream()
                .filter(t -> Math.abs(t.getAmount()) > 0)
                .sorted()
                .collect(Collectors.toList());
        int balance = 0;

        series.setName("Transactions");

        for (Transaction t : transactionsSortedFiltered) {
            balance += t.getAmount();
            series.getData().add(new XYChart.Data<>(t.getTime().toString(), balance));
        }

        return series;
    }
}
