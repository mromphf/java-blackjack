package main.domain;

import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Optional;
import java.util.function.Function;

public interface Assessment extends Function<Snapshot, Optional<Transaction>> {}
