package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import main.domain.Card;
import main.adapter.storage.FileSystem;

import java.util.Properties;
import java.util.Stack;

import static com.google.inject.name.Names.named;
import static java.lang.Integer.parseInt;
import static main.domain.Deck.freshlyShuffledDeck;

public class ConfigInjectionModule extends AbstractModule {

    private FileSystem fileSystem;

    public ConfigInjectionModule(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void configure() {
        final Properties config = fileSystem.loadConfig();
        final String deckName = (String) config.get("game.deckName");
        final int numDecks = parseInt((String) config.get("game.numDecks"));

        bind(Integer.class)
                .annotatedWith(named("numDecks"))
                .toInstance(numDecks);

        bind(new TypeLiteral<Stack<Card>>() {})
                .annotatedWith(named("deck"))
                .toInstance(deckName.equals("default") ? freshlyShuffledDeck(numDecks) : fileSystem.loadDeck(deckName));

    }
}
