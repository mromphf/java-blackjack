package main.adapter.log;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.time.LocalDateTime.now;
import static main.adapter.log.CsvUtil.appendToFile;
import static main.adapter.storage.FileFunctions.dateBasedLogFileName;

public class FileLogHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        final File f = new File("./log/" + dateBasedLogFileName(now()));
        appendToFile(f, String.format("%s: %s", record.getLevel(), record.getMessage()));
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
