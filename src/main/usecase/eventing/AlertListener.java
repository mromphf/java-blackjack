package main.usecase.eventing;

import javafx.scene.control.Alert;
import main.common.Identifiable;

public interface AlertListener extends Identifiable {
    void onAlertEvent(Event<Alert> event);
}
