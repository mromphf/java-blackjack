package main.usecase.eventing;

import main.common.Identifiable;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event<T> implements Identifiable {
    private final UUID sourceKey;
    private final LocalDateTime timestamp;
    private final Predicate predicate;
    private final T data;

    public Event(UUID sourceKey, LocalDateTime timestamp, Predicate predicate, T data) {
        this.sourceKey = sourceKey;
        this.timestamp = timestamp;
        this.predicate = predicate;
        this.data = data;
    }

    public boolean is(Predicate p) {
        return predicate.equals(p);
    }

    @Override
    public UUID getKey() {
        return sourceKey;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public T getData() {
        return data;
    }
}
