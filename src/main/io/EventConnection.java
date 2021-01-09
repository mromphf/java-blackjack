package main.io;

import main.usecase.Event;
import main.usecase.EventNetwork;

public abstract class EventConnection {

    protected EventNetwork eventNetwork;

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }

    public void post(Event e) {
        eventNetwork.post(e);
    }
}
