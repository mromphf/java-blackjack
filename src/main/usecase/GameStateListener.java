package main.usecase;

import main.domain.Snapshot;

public interface GameStateListener {
    void onUpdate(int balance, Snapshot snapshot);
}
