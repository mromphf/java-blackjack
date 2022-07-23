package main.usecase;

import main.domain.model.Action;
import main.domain.model.TableView;

public interface GameListener {
    TableView peek();
    TableView onAction(Action action);
}
