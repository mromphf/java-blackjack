package main.usecase;

import main.domain.model.Action;
import main.domain.model.Table;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StateRepository {

    void saveNewRound(final Table table);
    void saveLastActionTaken(
            final LocalDateTime timestamp,
            final UUID roundKey,
            final UUID accountKey,
            final Action action);
}
