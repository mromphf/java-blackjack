package main.usecase;

import main.domain.model.Snapshot;

public interface GameObserver {
    void onUpdate(Snapshot snapshot);
}
