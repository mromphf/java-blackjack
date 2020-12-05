package main.usecase;

public interface ControlListener {
    void onStartNewRound(int bet);
    void onMoveToBettingTable();
    void onStopPlaying();
    void onHit();
    void onDouble();
    void onStand();
    void onSettleHand();
    void onSplit();
}
