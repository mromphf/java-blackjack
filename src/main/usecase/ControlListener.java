package main.usecase;

public interface ControlListener {
    void onStartNewRound();
    void onBetPlaced(int bet);
    void onMoveToBettingTable();
    void onHit();
    void onDealerTurn();
}
