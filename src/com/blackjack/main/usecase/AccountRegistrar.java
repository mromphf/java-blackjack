package com.blackjack.main.usecase;

import com.blackjack.main.domain.model.Account;

public interface AccountRegistrar {

    void createNew(Account account);
}
