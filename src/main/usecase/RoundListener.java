package main.usecase;

public interface RoundListener {
    void onUpdate(GameState gameState);
    void onShowdown(GameState gameState);
}
