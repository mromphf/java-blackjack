package main.usecase.eventing;

import main.common.Identifiable;
import main.domain.Snapshot;

public interface SnapshotListener extends Identifiable {
    void onGameUpdate(Snapshot snapshot);
}
