package abeshutt.staracademy.item;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.item.renderer.CardItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.util.ISpecialItemModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;

import java.util.function.Consumer;

public class CardItem extends Item implements ISpecialItemModel {

    public CardItem() {
        super(new Settings().maxCount(1).fireproof());
    }

    @Override
    public void loadModels(Consumer<ModelIdentifier> consumer) {
        for(CardRarity rarity : CardRarity.values()) {
            consumer.accept(StarAcademyMod.mid("item/card/frame/" + rarity.asString(), "inventory"));
        }

        //TODO: load icons based on configs
    }

    @Override
    public SpecialItemRenderer getRenderer() {
        return CardItemRenderer.INSTANCE;
    }

}
