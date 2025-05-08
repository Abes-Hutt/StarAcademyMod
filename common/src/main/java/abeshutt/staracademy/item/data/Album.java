package abeshutt.staracademy.item.data;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.AttributeContext;

import java.util.UUID;

public class Album {

    private UUID uuid;
    private Card[] cards;

    public Album() {
        this.cards = new Card[25];
    }

    public void update(AttributeContext context) {
        Attribute<?> root = context.getRoot();
        root.remove(this.uuid);

        for(Card card : this.cards) {
            for(CardModifier modifier : card.getModifiers()) {
                root.add(modifier.get());
            }
        }
    }

}
