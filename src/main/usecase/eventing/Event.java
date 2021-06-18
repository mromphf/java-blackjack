package main.usecase.eventing;

import java.time.LocalDateTime;

public class Event<T> {
    private final LocalDateTime timestamp;
    private final Predicate predicate;
    private final T data;

    public Event(LocalDateTime timestamp, Predicate predicate, T data) {
        this.timestamp = timestamp;
        this.predicate = predicate;
        this.data = data;
    }

    public boolean is(Predicate p) {
        return predicate.equals(p);
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
