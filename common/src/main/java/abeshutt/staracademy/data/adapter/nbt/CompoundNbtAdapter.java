package abeshutt.staracademy.data.adapter.nbt;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CompoundNbtAdapter extends NbtAdapter<NbtCompound> {

    public CompoundNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public CompoundNbtAdapter asNullable() {
        return new CompoundNbtAdapter(true);
    }

    @Override
    protected void writeTagBits(NbtCompound value, BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(value.getSize(), buffer);

        for(String key : value.getKeys()) {
            Adapters.UTF_8.writeBits(key, buffer);
            Adapters.GENERIC_NBT.writeBits(value.get(key), buffer);
        }
    }

    @Override
    protected NbtCompound readTagBits(BitBuffer buffer) {
        NbtCompound compound = new NbtCompound();
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            String key = Adapters.UTF_8.readBits(buffer).orElseThrow();
            NbtElement tag = Adapters.GENERIC_NBT.readBits(buffer).orElseThrow();
            compound.put(key, tag);
        }

        return compound;
    }

    @Override
    protected void writeTagBytes(NbtCompound value, ByteBuf buffer) {
        Adapters.INT_SEGMENTED_3.writeBytes(value.getSize(), buffer);

        for(String key : value.getKeys()) {
            Adapters.UTF_8.writeBytes(key, buffer);
            Adapters.GENERIC_NBT.writeBytes(value.get(key), buffer);
        }
    }

    @Override
    protected NbtCompound readTagBytes(ByteBuf buffer) {
        NbtCompound compound = new NbtCompound();
        int size = Adapters.INT_SEGMENTED_3.readBytes(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            String key = Adapters.UTF_8.readBytes(buffer).orElseThrow();
            NbtElement tag = Adapters.GENERIC_NBT.readBytes(buffer).orElseThrow();
            compound.put(key, tag);
        }

        return compound;
    }

    @Override
    protected void writeTagData(NbtCompound value, DataOutput data) throws IOException {
        Adapters.INT_SEGMENTED_3.writeData(value.getSize(), data);

        for(String key : value.getKeys()) {
            Adapters.UTF_8.writeData(key, data);
            Adapters.GENERIC_NBT.writeData(value.get(key), data);
        }
    }

    @Override
    protected NbtCompound readTagData(DataInput data) throws IOException {
        NbtCompound compound = new NbtCompound();
        int size = Adapters.INT_SEGMENTED_3.readData(data).orElseThrow();

        for(int i = 0; i < size; i++) {
            String key = Adapters.UTF_8.readData(data).orElseThrow();
            NbtElement tag = Adapters.GENERIC_NBT.readData(data).orElseThrow();
            compound.put(key, tag);
        }

        return compound;
    }

    @Override
    protected NbtElement writeTagNbt(NbtCompound value) {
        return value.copy();
    }

    
    @Override
    protected NbtCompound readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtCompound tag ? tag.copy() : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtCompound value) {
        JsonObject json = new JsonObject();

        for(String key : value.getKeys()) {
            NbtElement element = value.get(key);
            Adapters.GENERIC_NBT.writeJson(element).ifPresent(tag -> json.add(key, tag));
        }

        return json;
    }

    @Override
    protected NbtCompound readTagJson(JsonElement json) {
        if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            try { return StringNbtReader.parse(primitive.getAsString()); }
            catch(CommandSyntaxException exception) { return null; }
        } else if(json instanceof JsonObject object) {
            NbtCompound nbt = new NbtCompound();

            for(String key : object.keySet()) {
                JsonElement element = object.get(key);
                Adapters.GENERIC_NBT.readJson(element).ifPresent(tag -> nbt.put(key, tag));
            }

            return nbt;
        }

        return null;
    }

}
