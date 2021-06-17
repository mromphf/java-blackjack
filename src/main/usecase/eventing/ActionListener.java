package main.usecase.eventing;

import main.domain.Action;

public interface ActionListener {
    void onActionEvent(Event<Action> event);
}
