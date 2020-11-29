package main.usecase;

import main.domain.GameState;

public interface GameStateListener {
    void onUpdate(GameState gameState);
}
