package main.usecase.eventing;

public abstract class EventConnection {

    protected EventNetwork eventNetwork = new EventNetwork();

    public void connectTo(EventNetwork eventNetwork) {
        this.eventNetwork = eventNetwork;
    }
}
