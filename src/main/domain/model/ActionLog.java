package main.domain.model;

import java.time.LocalDateTime;
import java.util.TreeMap;

public class ActionLog extends TreeMap<LocalDateTime, Action> {

    public static ActionLog emptyActionLog() {
        return new ActionLog();
    }
}
