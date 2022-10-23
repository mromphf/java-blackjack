package main.usecase;

import main.domain.model.TableView;

public interface TableObserver {
    default void newRoundStarted(TableView tableView) {}
    void onUpdate(TableView tableView);
}
