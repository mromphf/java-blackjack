package main.usecase;

import main.domain.Account;

public interface NavListener {
    void onStartNewRound(int bet);
    void onMoveToBettingTable();
    void onMoveToBettingTable(Account account);
    void onStopPlaying();
    void onViewHistory(Account account);
}
