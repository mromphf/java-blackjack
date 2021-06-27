package main.usecase.eventing;

import main.domain.Action;
import main.common.Identifiable;

public interface ActionListener extends Identifiable {
    void onActionEvent(Event<Action> event);
}
