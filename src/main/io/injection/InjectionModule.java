package main.io.injection;

import com.google.inject.AbstractModule;
import main.io.storage.AccountMemory;
import main.io.storage.Database;
import main.io.storage.FileSystem;
import main.io.storage.TransactionMemory;

import java.util.UUID;

import static com.google.inject.name.Names.named;
import static java.util.UUID.randomUUID;
import static main.io.ResourceLoader.getDirectoryMap;

public class InjectionModule extends AbstractModule {

    @Override
    public void configure() {
        bind(AccountMemory.class).to(Database.class);
        bind(TransactionMemory.class).to(Database.class);

        bind(String.class)
                .annotatedWith(named("logger"))
                .toInstance("Game Logger");

        bind(UUID.class)
                .annotatedWith(named("random"))
                .toInstance(randomUUID());

        bind(FileSystem.class)
                .toInstance(new FileSystem(getDirectoryMap()));
    }
}
