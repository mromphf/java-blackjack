package main.usecase.eventing;

import main.domain.Snapshot;

public interface GameStateListener {
    void onUpdate(Snapshot snapshot);
}
