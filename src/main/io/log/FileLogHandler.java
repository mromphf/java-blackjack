package main.io.log;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.time.LocalDateTime.now;
import static main.common.CsvUtil.appendToFile;
import static main.io.storage.FileFunctions.dateBasedLogFileName;

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
