package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.util.ItemUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

import static abeshutt.staracademy.StarAcademyMod.REGISTRIES;

public class ItemStackAdapter implements ISimpleAdapter<ItemStack, NbtElement, JsonElement> {

    private final boolean nullable;

    public ItemStackAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public ItemStackAdapter asNullable() {
        return new ItemStackAdapter(true);
    }

    @Override
    public void writeBits(ItemStack value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeBoolean(value.isEmpty());
            if (value.isEmpty()) return;
            Adapters.ITEM.writeBits(value.getItem(), buffer);
            Adapters.INT_SEGMENTED_7.writeBits(value.getCount(), buffer);
            Adapters.COMPOUND_NBT.asNullable().writeBits(ItemUtils.getNbt(value), buffer);
        }
    }

    @Override
    public Optional<ItemStack> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        } else if(buffer.readBoolean()) {
            return Optional.of(ItemStack.EMPTY);
        }

        Item item = Adapters.ITEM.readBits(buffer).orElse(Items.AIR);
        int count = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
        NbtCompound tag = Adapters.COMPOUND_NBT.asNullable().readBits(buffer).orElse(null);
        ItemStack stack = new ItemStack(item, count);
        ItemUtils.setNbt(stack, tag);
        return Optional.of(stack);
    }

    @Override
    public void writeBytes(ItemStack value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeBoolean(value.isEmpty());
            if (value.isEmpty()) return;
            Adapters.ITEM.writeBytes(value.getItem(), buffer);
            Adapters.INT_SEGMENTED_7.writeBytes(value.getCount(), buffer);
            Adapters.COMPOUND_NBT.asNullable().writeBytes(ItemUtils.getNbt(value), buffer);
        }
    }

    @Override
    public Optional<ItemStack> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        } else if(buffer.readBoolean()) {
            return Optional.of(ItemStack.EMPTY);
        }

        Item item = Adapters.ITEM.readBytes(buffer).orElse(Items.AIR);
        int count = Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow();
        NbtCompound tag = Adapters.COMPOUND_NBT.asNullable().readBytes(buffer).orElse(null);

        ItemStack stack = new ItemStack(item, count);
        ItemUtils.setNbt(stack, tag);
        return Optional.of(stack);
    }

    @Override
    public void writeData(ItemStack value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            data.writeBoolean(value.isEmpty());
            if (value.isEmpty()) return;
            Adapters.ITEM.writeData(value.getItem(), data);
            Adapters.INT_SEGMENTED_7.writeData(value.getCount(), data);
            Adapters.COMPOUND_NBT.asNullable().writeData(ItemUtils.getNbt(value), data);
        }
    }

    @Override
    public Optional<ItemStack> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        } else if(data.readBoolean()) {
            return Optional.of(ItemStack.EMPTY);
        }

        Item item = Adapters.ITEM.readData(data).orElse(Items.AIR);
        int count = Adapters.INT_SEGMENTED_7.readData(data).orElseThrow();
        NbtCompound tag = Adapters.COMPOUND_NBT.asNullable().readData(data).orElse(null);

        ItemStack stack = new ItemStack(item, count);
        ItemUtils.setNbt(stack, tag);
        return Optional.of(stack);
    }

    @Override
    public Optional<NbtElement> writeNbt(ItemStack value) {
        return value == null ? Optional.empty() : Optional.of(value.encode(REGISTRIES));
    }

    @Override
    public Optional<ItemStack> readNbt(NbtElement nbt) {
        return nbt instanceof NbtCompound compound ? ItemStack.fromNbt(REGISTRIES, nbt) : Optional.empty();
    }

    @Override
    public Optional<JsonElement> writeJson(ItemStack value) {
        if(value == null) {
            return Optional.empty();
        }

        JsonObject json = new JsonObject();
        Adapters.ITEM.writeJson(value.getItem()).ifPresent(element -> json.add("id", element));
        Adapters.COMPOUND_NBT.writeJson(ItemUtils.getNbt(value)).ifPresent(element -> json.add("nbt", element));
        Adapters.INT.writeJson(value.getCount()).ifPresent(element -> json.add("count", element));
        return Optional.of(json);
    }

    @Override
    public Optional<ItemStack> readJson(JsonElement json) {
        if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            return Adapters.ITEM.readJson(primitive).map(ItemStack::new);
        } else if(json instanceof JsonObject object) {
            ItemStack stack = new ItemStack(
                    Adapters.ITEM.readJson(object.get("id")).orElse(Items.AIR),
                    Adapters.INT.readJson(object.get("count")).orElse(1)
            );

            if(object.has("nbt")) {
                ItemUtils.setNbt(stack, Adapters.COMPOUND_NBT.readJson(object.get("nbt")).orElse(null));
            }

            return Optional.of(stack);
        }

        return Optional.empty();
    }

}
