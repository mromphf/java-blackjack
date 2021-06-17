package main.usecase.eventing;

import main.domain.Snapshot;

public interface SnapshotListener {
    void onGameUpdate(Snapshot snapshot);
}
