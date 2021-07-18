package main.usecase.eventing;

import main.common.Identifiable;
import main.usecase.Layout;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static main.usecase.Layout.HOME;
import static main.usecase.eventing.Predicate.LAYOUT_CHANGED;

public interface LayoutListener extends Identifiable {
    void onLayoutEvent(Event<Layout> event);

    default void onLayoutChangedTo(Layout layout, UUID key) {
        onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, layout));
    }
}
