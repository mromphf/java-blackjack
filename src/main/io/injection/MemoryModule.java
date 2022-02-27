package main.io.injection;

import com.google.inject.AbstractModule;
import main.io.storage.AccountMemory;
import main.io.storage.Database;
import main.io.storage.TransactionMemory;

public class MemoryModule extends AbstractModule {

    @Override
    public void configure() {
        bind(AccountMemory.class).to(Database.class);
        bind(TransactionMemory.class).to(Database.class);
    }
}
