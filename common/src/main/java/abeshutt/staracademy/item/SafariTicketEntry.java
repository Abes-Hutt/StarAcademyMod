package abeshutt.staracademy.item;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.IBitSerializable;
import abeshutt.staracademy.world.roll.IntRoll;
import com.google.gson.annotations.Expose;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.regex.Pattern;

public class SafariTicketEntry implements IBitSerializable {

    public static final Codec<SafariTicketEntry> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.STRING.fieldOf("name").forGetter(SafariTicketEntry::getName),
                Codecs.POSITIVE_INT.fieldOf("color").orElse(0xFFFFFF).forGetter(SafariTicketEntry::getColor),
                Codecs.ESCAPED_STRING.fieldOf("model").forGetter(SafariTicketEntry::getModel),
                Adapters.INT_ROLL.codec().fieldOf("model").forGetter(SafariTicketEntry::getModel)
                .apply(instance, SafariTicketEntry::new));
    });;

    @Expose private String name;
    @Expose private int color;
    @Expose private String model;
    @Expose private IntRoll time;

    public SafariTicketEntry() {

    }

    public SafariTicketEntry(String name, int color, IntRoll time, String model) {
        this.name = name;
        this.color = color;
        this.time = time;
        this.model = model;
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }

    public String getModel() {
        return this.model;
    }

    @Environment(EnvType.CLIENT)
    public ModelIdentifier getModelId() {
        String[] parts = this.model.split(Pattern.quote("#"));
        Identifier id = Identifier.tryParse(parts[0]);
        return new ModelIdentifier(id, parts.length == 1 ? "" : parts[1]);
    }

    public IntRoll getTime() {
        return this.time;
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.UTF_8.writeBits(this.name, buffer);
        Adapters.INT.writeBits(this.color, buffer);
        Adapters.UTF_8.writeBits(this.model, buffer);
        Adapters.INT_ROLL.writeBits(this.time, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.name = Adapters.UTF_8.readBits(buffer).orElseThrow();
        this.color = Adapters.INT.readBits(buffer).orElseThrow();
        this.model = Adapters.UTF_8.readBits(buffer).orElseThrow();
        this.time = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
    }

}
