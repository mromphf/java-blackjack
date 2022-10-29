package com.blackjack.main.usecase;

import com.blackjack.main.domain.model.TableView;

public interface TableObserver {
    default void newRoundStarted(TableView tableView) {}
    void onUpdate(TableView tableView);
}
