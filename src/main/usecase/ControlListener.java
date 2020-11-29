package main.usecase;

public interface ControlListener {
    void onStartNewRound();
    void onMoveToBettingTable();
    void onStopPlaying();
    void onBetPlaced(int bet);
    void onHit();
    void onDealerTurn();
    void onDouble();
}
