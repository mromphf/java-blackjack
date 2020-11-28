package main.usecase;

public interface OutcomeListener {
    void onShowdown(GameState gameState);
    void onDealerWins();
    void onPlayerWins();
    void onBust();
    void onPush();
}
