package main.usecase;

import main.domain.model.Account;
import java.util.Optional;

public interface SelectionService {

    Optional<Account> selectedAccount();
}
