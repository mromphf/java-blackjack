package main.usecase;

import main.domain.model.Action;
import main.domain.model.Table;

public interface Game {
    Table peek();
    Table action(Action action);
}
