package main.usecase;

import main.domain.model.Deck;
import main.domain.model.TableView;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StateRepository {
    void saveNewRound(final TableView tableView);
    void saveNewDeck(final TableView tableView);
    void saveNewCards(final Deck deck);
    void saveCardDrawn(final LocalDateTime timestamp,
                       final UUID handKey,
                       final UUID cardKey,
                       final UUID accountKey,
                       final UUID roundKey);
    void saveLastActionTaken(TableView tableView);
}
