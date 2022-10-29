package com.blackjack.main.adapter.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static com.blackjack.main.adapter.storage.Directory.LOG;

public class FileLogHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        final File f = new File(format("%s/%s", LOG.path(),dateBasedLogFileName(now())));

        try {
            final PrintWriter writer = new PrintWriter(new FileWriter(f, true));

            writer.println(format("%s: %s", record.getLevel(), record.getMessage()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    private static String dateBasedLogFileName(LocalDateTime t) {
        return format("%s.log", t.format(ISO_DATE));
    }
}
