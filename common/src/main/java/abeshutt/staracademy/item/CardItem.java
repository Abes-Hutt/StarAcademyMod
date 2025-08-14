package abeshutt.staracademy.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.card.CardData;
import abeshutt.staracademy.card.CardIconEntry;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModDataComponents;
import abeshutt.staracademy.init.ModItems;
import abeshutt.staracademy.item.renderer.CardItemRenderer;
import abeshutt.staracademy.item.renderer.SpecialItemRenderer;
import abeshutt.staracademy.util.ClientScheduler;
import abeshutt.staracademy.util.ColorBlender;
import abeshutt.staracademy.util.ISpecialItemModel;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CardItem extends Item implements ISpecialItemModel, Equipment {

    public CardItem() {
        super(new Settings().maxCount(1).fireproof());
    }

    @Override
    public Text getName(ItemStack stack) {
        CardData data = CardItem.get(stack).orElse(null);
        if(data == null) return super.getName(stack);
        CardIconEntry entry = ModConfigs.CARD_ICONS.get(data.getIcon()).orElse(null);
        if(entry == null) return super.getName(stack);
        int[] colors = entry.getColors(data.getRarity()).orElse(null);
        if(colors == null) return super.getName(stack);

        MutableText text = Text.empty();
        text.append(Text.translatable("text.academy.card.name.prefix"));
        text.append(Text.translatable(entry.getName()));
        text.append(Text.translatable("text.academy.card.name.suffix"));

        ColorBlender blender = new ColorBlender(2.0F);

        for(int color : colors) {
            blender.add(color, 300.0F);
        }

        MutableText formatted = Text.empty();
        String raw = text.getString();
        double time = 0.0F;

        if(Platform.getEnv() != EnvType.SERVER) {
            time = ClientScheduler.getTick(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));
        }

        int count = 0;

        for(int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            formatted = formatted.append(Text.literal(String.valueOf(c))
                    .setStyle(Style.EMPTY.withColor(blender.getColor(time + count * 10.0F))));
            if(c != ' ') count++;
        }

        return formatted;
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
