package main.adapter.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;

public class FileFunctions {

    public static String dateBasedLogFileName(LocalDateTime t) {
        return format("%s.log", t.format(ISO_DATE));
    }

    public static String fileToJson(File f) throws IOException {
        final Scanner sc = new Scanner(f);
        final StringBuilder sb = new StringBuilder();

        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }

        return sb.toString();
    }

}
