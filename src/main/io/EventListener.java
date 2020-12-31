package main.io;

import main.usecase.EventNetwork;

public abstract class EventListener {

    protected EventNetwork eventNetwork;

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }
}
