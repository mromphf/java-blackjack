package main.domain.function;

import main.domain.model.Table;
import main.domain.model.Transaction;

import java.util.Optional;
import java.util.function.Function;

public interface Assessment extends Function<Table, Optional<Transaction>> {}
