package main.usecase.eventing;

import main.domain.Action;
import main.domain.Identifiable;

public interface ActionListener extends Identifiable {
    void onActionEvent(Event<Action> event);
}
