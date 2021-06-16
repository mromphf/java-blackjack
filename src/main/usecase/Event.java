package main.usecase;

public class Event<T> {
    private final Predicate predicate;
    private final T data;

    public Event(Predicate predicate, T data) {
        this.predicate = predicate;
        this.data = data;
    }

    public boolean is(Predicate p) {
        return predicate.equals(p);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public T getData() {
        return data;
    }
}
