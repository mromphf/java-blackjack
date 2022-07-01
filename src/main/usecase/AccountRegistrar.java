package main.usecase;

import main.domain.model.Account;

public interface AccountRegistrar {

    void createNew(Account account);
}
