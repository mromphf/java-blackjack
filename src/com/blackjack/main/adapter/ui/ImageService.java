package com.blackjack.main.adapter.ui;

import com.blackjack.main.adapter.graphics.Moving;
import com.blackjack.main.domain.model.Card;
import javafx.scene.image.Image;

import java.util.Collection;
import java.util.List;

public interface ImageService {
    List<Image> fromCards(Collection<Card> cards);
    Collection<Moving<Image>> reelLeft();
    Collection<Moving<Image>> reelRight();
}
