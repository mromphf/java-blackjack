package main.usecase;

public interface OutcomeListener {
    void onDealerWins(GameState gameState);
    void onPlayerWins(GameState gameState);
    void onBust(GameState gameState);
    void onPush(GameState gameState);
}
