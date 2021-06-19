package main.usecase.eventing;

import main.domain.Transaction;

import java.util.Collection;
import java.util.UUID;

public interface TransactionResponder {
    Collection<Transaction> requestTransactionsByKey(UUID accountKey);
}
