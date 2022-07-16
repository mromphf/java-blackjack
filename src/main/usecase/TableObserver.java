package main.usecase;

import main.domain.model.TableView;

public interface TableObserver {
    void onUpdate(TableView tableView);
}
