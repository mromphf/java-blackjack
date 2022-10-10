package main.usecase;

import main.domain.process.Round;

public interface RecordRepository {

    void saveNewRound(final Round round);
    void saveLastActionTaken(final Round round);
}
