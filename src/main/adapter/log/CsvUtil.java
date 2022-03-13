package main.adapter.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvUtil {
    public static void appendToFile(File file, String row) {
        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(file, true));

            writer.println(row);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
