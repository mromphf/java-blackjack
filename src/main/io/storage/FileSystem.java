package main.io.storage;

import com.oracle.javafx.jmx.json.JSONDocument;
import com.oracle.javafx.jmx.json.impl.JSONStreamReaderImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class FileSystem {
    public static String dateBasedFileName(LocalDateTime t) {
        return String.format("%s-%s-%s.csv", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }

    public static File[] allFilesInDir(File directory) {
        return Objects.requireNonNull(directory.listFiles());
    }

    public static JSONDocument fileToJsonDocument(File f) throws IOException {
        final FileReader fileReader = new FileReader(f);
        final JSONStreamReaderImpl jsonStreamReader = new JSONStreamReaderImpl(fileReader);
        final JSONDocument document = jsonStreamReader.build();

        fileReader.close();
        jsonStreamReader.close();

        return document;
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
