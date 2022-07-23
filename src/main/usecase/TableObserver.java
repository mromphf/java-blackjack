package main.usecase;

import main.domain.model.Table;

public interface TableObserver {
    void onUpdate(Table table);
}
