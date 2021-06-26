package main.usecase.eventing;

import javafx.scene.control.Alert;
import main.domain.Identifiable;

public interface AlertListener extends Identifiable {
    void onAlertEvent(Event<Alert> event);
}
