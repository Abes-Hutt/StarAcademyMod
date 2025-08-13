package abeshutt.staracademy.card;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.AddAttribute;
import abeshutt.staracademy.attribute.again.Attribute;
import abeshutt.staracademy.attribute.again.AttributeContext;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.any;

public class CardModifier implements ISerializable<NbtCompound, JsonObject> {

    private String source;
    private Attribute<?> attribute;

    public CardModifier() {

    }

    public CardModifier(String source, Attribute<?> attribute) {
        this.source = source;
        this.attribute = attribute;
    }

    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        MutableText text = Text.empty();

        if(this.attribute instanceof AddAttribute<?> add) {
            Object value = add.getValue().get(Option.absent(),
                    new AttributeContext(null)).orElse(null);

            if(value != null) {
                text.append(Text.literal("+"));
                text.append(value.toString());
            }
        }

        tooltip.add(text);
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UTF_8.writeNbt(this.source).ifPresent(tag -> nbt.put("source", tag));
            Adapters.ATTRIBUTE.writeNbt(this.attribute, any()).ifPresent(tag -> nbt.put("attribute", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.source = Adapters.UTF_8.readNbt(nbt.get("source")).orElseThrow();
        this.attribute = Adapters.ATTRIBUTE.readNbt(nbt.get("attribute"), any()).orElseThrow();
    }

}
