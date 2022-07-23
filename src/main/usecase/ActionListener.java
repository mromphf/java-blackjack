package main.usecase;

import main.domain.model.Action;

public interface ActionListener {
    void onAction(Action action);
}
