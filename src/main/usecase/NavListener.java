package main.usecase;

public interface NavListener {
    void onStartNewRound(int bet);
    void onMoveToBettingTable();
    void onStopPlaying();
}
