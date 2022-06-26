package main;

import com.google.inject.Injector;
import main.adapter.storage.Storage;
import main.usecase.LayoutManager;

public class AppRoot {

    private final Injector baseInjector;
    private final Injector fxmlInjector;

    public AppRoot(Injector baseInjector, Injector fxmlInjector) {
        this.baseInjector = baseInjector;
        this.fxmlInjector = fxmlInjector;
    }

    public void init() {
        final Storage storage = baseInjector.getInstance(Storage.class);

        final LayoutManager layoutManager = fxmlInjector.getInstance(LayoutManager.class);

        layoutManager.initializeLayout();

        new Thread(storage::loadAllAccounts, "Account Load Thread").start();
        new Thread(storage::loadAllTransactions, "Transaction Load Thread").start();
    }
}
