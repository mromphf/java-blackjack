package main.usecase;

import main.io.EventConnection;

import java.util.Collection;
import java.util.LinkedList;

public class EventNetwork {

    private final Collection<EventListener> eventListeners = new LinkedList<>();

    public EventNetwork(Collection<EventConnection> connections) {
        for (EventConnection connection : connections) {

            if (connection instanceof EventListener) {
                eventListeners.add((EventListener) connection);
            }
        }
    }

    public void registerEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void post(Event e) {
        eventListeners.forEach(l -> l.listen(e));
    }
}
