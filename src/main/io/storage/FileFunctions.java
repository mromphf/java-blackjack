package main.io.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

public class FileFunctions {
    public static String dateBasedFileName(LocalDateTime t) {
        return String.format("%s-%s-%s.csv", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }

    public static File[] allFilesInDir(File directory) {
        return Objects.requireNonNull(directory.listFiles());
    }

    public static String fileToJson(File f) throws IOException {
        final Scanner sc = new Scanner(f);
        final StringBuilder sb = new StringBuilder();

        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }

        return sb.toString();
    }

    public static Collection<String> readCsvLines(File csvFile) throws FileNotFoundException {
        final Scanner sc = new Scanner(csvFile);
        final Collection<String> strings = new ArrayList<>();

        // Assume header row
        sc.nextLine();

        while (sc.hasNextLine()) {
            strings.add(sc.nextLine());
        }

        return strings;
    }
}
