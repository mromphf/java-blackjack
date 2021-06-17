package main.usecase.eventing;

public interface Responder {
    Message fulfill(Predicate elm);
}
