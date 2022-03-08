package main.usecase.eventing;

import main.usecase.Layout;

public interface LayoutListener {
    void onLayoutEvent(Layout event);
}
