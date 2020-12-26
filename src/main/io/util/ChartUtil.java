package main.io.util;

import javafx.scene.chart.XYChart;
import main.domain.Transaction;

import java.util.Set;

public class ChartUtil {

    public static XYChart.Series<String, Number> transactionsToSeries(Set<Transaction> transactions) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Transactions");

        for (Transaction t : transactions) {
            series.getData().add(new XYChart.Data<>(t.getTime().toString(), t.getAmount()));
        }

        return series;
    }
}
