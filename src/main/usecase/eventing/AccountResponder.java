package main.usecase.eventing;

import main.domain.Account;

public interface AccountResponder {
    Account requestSelectedAccount(Predicate predicate);
}
