package abeshutt.staracademy.item.data;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.NumberAttribute;
import abeshutt.staracademy.math.Rational;

import java.util.UUID;

public class CardAlbum {

    private UUID uuid;
    private Card[] cards;

    public CardAlbum() {
        this.cards = new Card[25];
    }

    public void update(Attribute<?> root) {
        root.remove(this.uuid);

        for(Card card : this.cards) {
            for(CardModifier modifier : card.getModifiers()) {
                //root.add(this.uuid, modifier.get());
            }
        }
    }

}
