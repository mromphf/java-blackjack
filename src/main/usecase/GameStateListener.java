package main.usecase;

import main.domain.StateSnapshot;

public interface GameStateListener {
    void onUpdate(StateSnapshot stateSnapshot);
}
