package main.usecase.eventing;

import java.util.LinkedList;

import static java.util.UUID.randomUUID;

public abstract class EventConnection {

    protected EventNetwork eventNetwork = new EventNetwork(randomUUID(), new LinkedList<>());

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }
}
