package abeshutt.staracademy.item;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.item.renderer.CardItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.util.ISpecialItemModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class CardItem extends Item implements ISpecialItemModel {

    public CardItem() {
        super(new Settings().maxCount(1).fireproof());
    }

    @Override
    public void loadModels(Consumer<ModelIdentifier> consumer) {
        for(CardRarity rarity : CardRarity.values()) {
            consumer.accept(StarAcademyMod.mid("card/frame/" + rarity.asString(), "inventory"));
        }

        //TODO: load icons based on configs
        consumer.accept(StarAcademyMod.mid("card/icon/bulbasaur", "inventory"));
        consumer.accept(StarAcademyMod.mid("card/icon/gengar", "inventory"));
        consumer.accept(StarAcademyMod.mid("card/icon/ivysaur", "inventory"));
        consumer.accept(StarAcademyMod.mid("card/icon/venusaur", "inventory"));
    }

    @Override
    public SpecialItemRenderer getRenderer() {
        return CardItemRenderer.INSTANCE;
    }

    public static int getIndex(ItemStack stack) {
        Integer value = stack.get(ModDataComponents.CARD_INDEX.get());
        return value == null ? 0 : value;
    }

}
