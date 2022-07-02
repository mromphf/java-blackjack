package main.adapter.log;

import java.io.File;
import java.time.LocalDateTime;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static main.adapter.log.CsvUtil.appendToFile;
import static main.adapter.storage.Directory.LOG;

public class FileLogHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        final File f = new File(format("%s/%s", LOG.path(),dateBasedLogFileName(now())));
        appendToFile(f, format("%s: %s", record.getLevel(), record.getMessage()));
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
