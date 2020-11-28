package main.io;

import main.usecase.ControlListener;

import java.util.ArrayList;
import java.util.Collection;

public abstract class RootController {

    protected final Collection<ControlListener> controlListeners;

    public RootController() {
        controlListeners = new ArrayList<>();
    }

    public void registerControlListener(ControlListener controlListener) {
        this.controlListeners.add(controlListener);
    }
}
