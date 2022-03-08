package main.usecase.eventing;

import javafx.scene.control.Alert;

public interface AlertListener {
    void onAlertEvent(Alert event);
}
