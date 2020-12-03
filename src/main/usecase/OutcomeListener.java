package main.usecase;

import main.domain.Snapshot;

public interface OutcomeListener {
    void onDealerWins(Snapshot snapshot);
    void onPlayerWins(Snapshot snapshot);
    void onBust(Snapshot snapshot);
    void onPush(Snapshot snapshot);
}
