package main.usecase.eventing;

import main.common.Identifiable;
import main.usecase.Layout;

public interface LayoutListener extends Identifiable {
    void onLayoutEvent(Event<Layout> event);
}
