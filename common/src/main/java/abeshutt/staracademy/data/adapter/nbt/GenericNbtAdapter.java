package abeshutt.staracademy.data.adapter.nbt;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.number.BoundedByteAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class GenericNbtAdapter extends NbtAdapter<NbtElement> {

    protected static final BoundedByteAdapter NBT_ID = new BoundedByteAdapter((byte)0, (byte)(Adapters.NBT.length - 1), false);

    public GenericNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public GenericNbtAdapter asNullable() {
        return new GenericNbtAdapter(true);
    }

    @Override
    protected void writeTagBits(NbtElement value, BitBuffer buffer) {
        NBT_ID.writeBits(value.getType(), buffer);
        Adapters.NBT[value.getType()].writeBits(value, buffer);
    }

    @Override
    protected NbtElement readTagBits(BitBuffer buffer) {
        return (NbtElement)Adapters.NBT[NBT_ID.readBits(buffer).orElseThrow()].readBits(buffer).orElseThrow();
    }

    @Override
    protected void writeTagBytes(NbtElement value, ByteBuf buffer) {
        NBT_ID.writeBytes(value.getType(), buffer);
        Adapters.NBT[value.getType()].writeBytes(value, buffer);
    }

    @Override
    protected NbtElement readTagBytes(ByteBuf buffer) {
        return (NbtElement)Adapters.NBT[NBT_ID.readBytes(buffer).orElseThrow()].readBytes(buffer).orElseThrow();
    }

    @Override
    protected void writeTagData(NbtElement value, DataOutput data) throws IOException {
        NBT_ID.writeData(value.getType(), data);
        Adapters.NBT[value.getType()].writeData(value, data);
    }

    @Override
    protected NbtElement readTagData(DataInput data) throws IOException {
        return (NbtElement)Adapters.NBT[NBT_ID.readData(data).orElseThrow()].readData(data).orElseThrow();
    }
    
    @Override
    protected NbtElement writeTagNbt(NbtElement value) {
        return (NbtElement)Adapters.NBT[value.getType()].writeNbt(value).orElse(null);
    }

    @Override
    protected NbtElement readTagNbt(NbtElement nbt) {
        return (NbtElement)Adapters.NBT[nbt.getType()].readNbt(nbt).orElse(null);
    }

    @Override
    protected JsonElement writeTagJson(NbtElement value) {
        return (JsonElement)Adapters.NBT[value.getType()].writeJson(value).orElse(null);
    }
    
    @Override
    protected NbtElement readTagJson(JsonElement json) {
        if(json instanceof JsonPrimitive value) {
            if(value.isNumber()) {
                return Adapters.NUMERIC_NBT.readJson(value).orElse(null);
            } else if(value.isString()) {
                return Adapters.STRING_NBT.readJson(value).orElse(null);
            } else if(value.isBoolean()) {
                return Adapters.BOOLEAN.writeNbt(value.getAsBoolean()).orElse(null);
            }
        } else if(json instanceof JsonObject value) {
            return Adapters.COMPOUND_NBT.readJson(value).orElse(null);
        } else if(json instanceof JsonArray value) {
            return Adapters.COLLECTION_NBT.readJson(value).orElse(null);
        }

        return null;
    }

}
