package main.usecase.eventing;

import main.domain.Account;

import java.util.Optional;

public interface AccountResponder {
    Optional<Account> requestSelectedAccount(Predicate predicate);
}
