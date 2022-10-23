package main.domain.function;

import main.domain.model.TableView;
import main.domain.model.Transaction;

import java.util.Optional;
import java.util.function.Function;

public interface Assessment extends Function<TableView, Optional<Transaction>> {}
