package main.usecase;

import main.domain.Snapshot;

public interface OutcomeListener {
    void onOutcomeDecided(Snapshot snapshot, Outcome outcome);
}
