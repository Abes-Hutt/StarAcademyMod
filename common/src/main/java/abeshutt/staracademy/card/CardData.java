package abeshutt.staracademy.card;

import abeshutt.staracademy.CardRarity;
import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardData implements ISerializable<NbtCompound, JsonObject> {

    private String icon;
    private CardRarity rarity;
    private int grade;
    private final List<CardModifier> modifiers;

    public CardData() {
        this.modifiers = new ArrayList<>();
    }

    public CardData(String icon, CardRarity rarity, int grade, List<CardModifier> modifiers) {
        this.modifiers = modifiers;
        this.grade = grade;
        this.rarity = rarity;
        this.icon = icon;
    }

    public String getIcon() {
        return this.icon;
    }

    public CardRarity getRarity() {
        return this.rarity;
    }

    public int getGrade() {
        return this.grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;

        for(CardModifier modifier : this.modifiers) {
            modifier.setGrade(this.grade);
        }
    }

    public void attach(Attribute<?> root) {
        for(CardModifier modifier : this.modifiers) {
           modifier.attach(root);
           modifier.setGrade(this.grade);
        }
    }

    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.empty()
                .append(Text.translatable("text.academy.card.rarity"))
                .append(Text.literal(": "))
                .append(Text.translatable("text.academy.card.rarity." + this.rarity.asString()).setStyle(Style.EMPTY.withColor(this.rarity.getColor()))));

        if(this.grade == 0) {
            tooltip.add(Text.empty()
                    .append(Text.translatable("text.academy.card.grade").formatted(Formatting.GRAY))
                    .append(Text.literal(": ???").formatted(Formatting.GRAY)));
        } else {
            tooltip.add(Text.empty()
                    .append(Text.translatable("text.academy.card.grade"))
                    .append(Text.literal(": "))
                    .append(Text.translatable("text.academy.card.grade." + this.grade)));
        }

        //tooltip.add(Text.empty());

        for(CardModifier modifier : this.modifiers) {
           modifier.appendTooltip(stack, this.grade, context, tooltip, type);
        }
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UTF_8.writeNbt(this.icon).ifPresent(tag -> nbt.put("icon", tag));
            Adapters.CARD_RARITY.writeNbt(this.rarity).ifPresent(tag -> nbt.put("rarity", tag));

            if(this.grade != 0) {
                Adapters.INT.writeNbt(this.grade).ifPresent(tag -> nbt.put("grade", tag));
            }

            NbtList modifiers = new NbtList();

            for(CardModifier modifier : this.modifiers) {
               modifier.writeNbt().ifPresent(modifiers::add);
            }

            nbt.put("modifiers", modifiers);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.icon = Adapters.UTF_8.readNbt(nbt.get("icon")).orElseThrow();
        this.rarity = Adapters.CARD_RARITY.readNbt(nbt.get("rarity")).orElseThrow();
        this.grade = Adapters.INT.readNbt(nbt.get("grade")).orElse(0);
        this.modifiers.clear();

        if(nbt.get("modifiers") instanceof NbtList modifiers) {
            for(NbtElement element : modifiers) {
               if(!(element instanceof NbtCompound entry)) continue;
               CardModifier modifier = new CardModifier();
               modifier.readNbt(entry);
               this.modifiers.add(modifier);
            }
        }
    }

}
