package main.io;

import main.usecase.EventListener;
import main.usecase.EventNetwork;

public abstract class EventConnection implements EventListener {

    protected EventNetwork eventNetwork;

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }
}
