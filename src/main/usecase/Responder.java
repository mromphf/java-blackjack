package main.usecase;

public interface Responder {
    Message fulfill(Predicate elm);
}
