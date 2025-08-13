package abeshutt.staracademy.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardData;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.item.renderer.CardItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.util.ISpecialItemModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CardItem extends Item implements ISpecialItemModel, Equipment {

    public CardItem() {
        super(new Settings().maxCount(1).fireproof());
    }

    @Override
    public void loadModels(Stream<Identifier> unbakedModels, Consumer<ModelIdentifier> loader) {
        unbakedModels.forEach(id -> {
            if (id.getNamespace().equals(StarAcademyMod.ID) && id.getPath().startsWith("card")) {
                loader.accept(StarAcademyMod.mid(id, "inventory"));
            }
        });
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        CardItem.get(stack).ifPresent(card -> {
            card.appendTooltip(stack, context, tooltip, type);
        });
    }

    @Override
    public SpecialItemRenderer getRenderer() {
        return CardItemRenderer.INSTANCE;
    }

    public static Optional<CardData> get(ItemStack stack) {
        return Optional.ofNullable(stack.getOrDefault(ModDataComponents.CARD.get(), null));
    }

    public static void set(ItemStack stack, CardData data) {
        stack.set(ModDataComponents.CARD.get(), data);
    }

    public static ItemStack of(CardData data) {
        ItemStack stack = new ItemStack(ModItems.CARD);
        CardItem.set(stack, data);
        return stack;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

}
