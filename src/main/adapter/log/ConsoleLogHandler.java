package main.adapter.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ConsoleLogHandler extends Handler {

    @Override
    public void publish(LogRecord record) {
        System.out.printf("%s: %s%n", record.getLevel(), record.getMessage());
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
