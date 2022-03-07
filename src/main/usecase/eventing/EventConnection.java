package main.usecase.eventing;

import static java.util.UUID.randomUUID;

public abstract class EventConnection {

    protected EventNetwork eventNetwork = new EventNetwork(randomUUID());

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }
}
