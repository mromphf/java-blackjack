package main.usecase.eventing;

public abstract class EventConnection {

    protected EventNetwork eventNetwork = null;

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }
}
