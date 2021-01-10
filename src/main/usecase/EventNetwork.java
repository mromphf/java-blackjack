package main.usecase;


import java.util.Collection;

public class EventNetwork {

    private final Collection<EventListener> eventListeners;

    public EventNetwork(Collection<EventListener> listeners) {
        eventListeners = listeners;
    }

    public void post(Event e) {
        eventListeners.forEach(l -> l.listen(e));
    }
}
