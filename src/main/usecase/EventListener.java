package main.usecase;

public interface EventListener {
    void connectTo(EventNetwork eventNetwork);
    void listen(Event e);
}
