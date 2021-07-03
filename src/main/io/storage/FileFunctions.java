package main.io.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;

public class FileFunctions {
    public static String dateBasedCsvFileName(LocalDateTime t) {
        return format("%s.csv", t.format(ISO_DATE));
    }

    public static String dateBasedLogFileName(LocalDateTime t) {
        return format("%s.log", t.format(ISO_DATE));
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

    public static Collection<String> readCsvLines(File csvFile)  {
        final Scanner sc;
        final Collection<String> strings = new ArrayList<>();

        try {
            sc = new Scanner(csvFile);

            if (sc.hasNextLine()) {
                // Skip header row
                sc.nextLine();
            }

            while (sc.hasNextLine()) {
                strings.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return strings;
    }
}
