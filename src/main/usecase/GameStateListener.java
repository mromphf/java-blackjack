package main.usecase;

import main.domain.Snapshot;

public interface GameStateListener {
    void onUpdate(Snapshot snapshot);
}
