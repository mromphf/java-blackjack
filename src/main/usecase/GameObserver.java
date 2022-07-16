package main.usecase;

import main.domain.model.TableView;

public interface GameObserver {
    void onUpdate(TableView tableView);
}
