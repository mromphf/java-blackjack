package main.usecase.eventing;

public abstract class EventConnection {

    protected EventNetwork eventNetwork;

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }
}
