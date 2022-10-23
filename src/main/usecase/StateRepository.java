package main.usecase;

import main.domain.model.Deck;
import main.domain.model.Table;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StateRepository {
    void saveNewRound(final Table table);
    void saveNewDeck(final Table table);
    void saveNewCards(final Deck deck);
    void saveCardDrawn(final LocalDateTime timestamp,
                       final UUID handKey,
                       final UUID cardKey,
                       final UUID accountKey,
                       final UUID roundKey);
    void saveLastActionTaken(Table table);
}
