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

    public void registerActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    public void registerNavListener(NavListener navListener) {
        navListeners.add(navListener);
    }
}
