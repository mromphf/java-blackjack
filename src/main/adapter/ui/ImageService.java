package main.adapter.ui;

import javafx.scene.image.Image;
import main.adapter.graphics.Moving;
import main.domain.model.Card;
import main.domain.model.TableView;

import java.util.Collection;
import java.util.List;

public interface ImageService {
    List<Image> fromAllCards(TableView tableView);
    List<Image> fromPlayerCards(TableView tableView);
    List<Image> fromDealerCards(TableView tableView);
    List<Image> fromCards(Collection<Card> cards, boolean outcomeResolved);
    Collection<Moving<Image>> reelLeft();
    Collection<Moving<Image>> reelRight();
}
