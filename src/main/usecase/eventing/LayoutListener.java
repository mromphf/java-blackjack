package main.usecase.eventing;

import main.domain.Identifiable;
import main.usecase.Layout;

public interface LayoutListener extends Identifiable {
    void onLayoutEvent(Event<Layout> event);
}
