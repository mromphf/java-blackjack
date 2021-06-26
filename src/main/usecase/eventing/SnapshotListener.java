package main.usecase.eventing;

import main.domain.Identifiable;
import main.domain.Snapshot;

public interface SnapshotListener extends Identifiable {
    void onGameUpdate(Snapshot snapshot);
}
