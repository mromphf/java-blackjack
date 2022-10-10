package main.usecase;

import main.domain.model.Table;

public interface StateRepository {

    void saveNewRound(final Table table);
    void saveLastActionTaken(final Table table);
}
