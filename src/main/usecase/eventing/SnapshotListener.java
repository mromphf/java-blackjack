package main.usecase.eventing;

import main.domain.model.Snapshot;

public interface SnapshotListener {
    void onGameUpdate(Snapshot snapshot);
}
