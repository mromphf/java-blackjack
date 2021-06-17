package main.usecase.eventing;

public interface EventListener {
    void onEvent(Message message);
}
