package main.usecase.eventing;

import main.domain.Account;

public interface Responder {
    Account fulfill(Predicate predicate);
}
