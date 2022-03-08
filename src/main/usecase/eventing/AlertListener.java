package main.usecase.eventing;

import javafx.scene.control.Alert;
import main.common.Identifiable;

public interface AlertListener {
    void onAlertEvent(Alert event);
}
