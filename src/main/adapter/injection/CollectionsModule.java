package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.usecase.Layout;

import java.io.IOException;
import java.util.Map;

import static com.google.inject.name.Names.named;
import static java.util.stream.Collectors.toMap;
import static main.adapter.injection.Bindings.LAYOUT_MAP;
import static main.adapter.storage.FileSystem.resourceMap;
import static main.adapter.storage.FileSystem.resourcePathMap;
import static main.usecase.Layout.HOME;

public class CollectionsModule extends AbstractModule {

    final Injector baseInjector;

    public CollectionsModule(Injector baseInjector) {
        this.baseInjector = baseInjector;
    }

    @Override
    public void configure() {
    }
}
