package main.usecase;

import main.domain.model.Snapshot;

public interface SnapshotListener {
    void onGameUpdate(Snapshot snapshot);
}
