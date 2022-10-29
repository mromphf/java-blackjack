package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.TableView;
import com.blackjack.main.domain.model.Transaction;

import java.util.Optional;
import java.util.function.Function;

public interface Assessment extends Function<TableView, Optional<Transaction>> {}
