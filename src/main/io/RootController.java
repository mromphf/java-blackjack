package main.io;

import main.usecase.ControlListener;
import main.usecase.NavListener;

import java.util.Collection;
import java.util.LinkedList;

public abstract class RootController {

    protected final Collection<ControlListener> controlListeners;
    protected final Collection<NavListener> navListeners;

    public RootController() {
        controlListeners = new LinkedList<>();
        navListeners = new LinkedList<>();
    }

    public void registerControlListener(ControlListener controlListener) {
        this.controlListeners.add(controlListener);
    }

    public void registerNavListener(NavListener navListener) {
        this.navListeners.add(navListener);
    }
}
