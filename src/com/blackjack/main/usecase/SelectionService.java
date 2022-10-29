package com.blackjack.main.usecase;

import com.blackjack.main.domain.model.Account;
import java.util.Optional;

public interface SelectionService {

    Optional<Account> selectedAccount();
}
