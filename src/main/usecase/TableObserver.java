package main.usecase;

import main.domain.model.Table;

public interface TableObserver {
    default void newRoundStarted(Table table) {}
    void onUpdate(Table table);
}
