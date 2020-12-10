package main.io;

import main.usecase.ActionListener;
import main.usecase.NavListener;

import java.util.Collection;
import java.util.LinkedList;

public abstract class RootController {

    protected final Collection<ActionListener> actionListeners;
    protected final Collection<NavListener> navListeners;

    public RootController() {
        actionListeners = new LinkedList<>();
        navListeners = new LinkedList<>();
    }

    public void registerControlListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
    }

    public void registerNavListener(NavListener navListener) {
        this.navListeners.add(navListener);
    }
}
