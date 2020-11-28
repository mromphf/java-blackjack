package main.usecase;

public interface ControlListener {
    void onStartGame();
    void onBetPlaced(int bet);
    void onMoveToBettingTable();
}
