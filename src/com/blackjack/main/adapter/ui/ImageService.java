package com.blackjack.main.adapter.ui;

import com.blackjack.main.adapter.graphics.Moving;
import com.blackjack.main.domain.model.Card;
import javafx.scene.image.Image;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface ImageService {
    List<Image> fromCards(Stream<Card> cards);
    Collection<Moving<Image>> reelLeft();
    Collection<Moving<Image>> reelRight();
}
