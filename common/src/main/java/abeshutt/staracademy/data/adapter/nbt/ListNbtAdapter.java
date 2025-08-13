package abeshutt.staracademy.data.adapter.nbt;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ListNbtAdapter extends NbtAdapter<NbtList> {

    protected static final String[] ID_TO_KEY = new String[] {
        "END",
        "BYTE",
        "SHORT",
        "INT",
        "LONG",
        "FLOAT",
        "DOUBLE",
        "BYTE_ARRAY",
        "STRING",
        "LIST",
        "COMPOUND",
        "INT_ARRAY",
        "LONG_ARRAY"
    };

    protected static final Object2IntMap<String> KEY_TO_ID = new Object2IntOpenHashMap<>();

    static {
        for(int i = 0; i < ID_TO_KEY.length; i++) {
            KEY_TO_ID.put(ID_TO_KEY[i], i);
        }

        KEY_TO_ID.defaultReturnValue(-1);
    }

    public ListNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public ListNbtAdapter asNullable() {
        return new ListNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(NbtList value, BitBuffer buffer) {
        GenericNbtAdapter.NBT_ID.writeBits(value.getHeldType(), buffer);
        Adapters.INT_SEGMENTED_3.writeBits(value.size(), buffer);

        for(NbtElement element : value) {
            Adapters.NBT[value.getHeldType()].writeBits(element, buffer);
        }
    }

    @Override
    protected NbtList readTagBits(BitBuffer buffer) {
        NbtList list = new NbtList();
        byte id = GenericNbtAdapter.NBT_ID.readBits(buffer).orElseThrow();
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            list.add((NbtElement)Adapters.NBT[id].readBits(buffer).orElseThrow());
        }

        return list;
    }

    @Override
    protected void writeTagBytes(NbtList value, ByteBuf buffer) {
        GenericNbtAdapter.NBT_ID.writeBytes(value.getHeldType(), buffer);
        Adapters.INT_SEGMENTED_3.writeBytes(value.size(), buffer);

        for(NbtElement element : value) {
            Adapters.NBT[value.getHeldType()].writeBytes(element, buffer);
        }
    }

    @Override
    protected NbtList readTagBytes(ByteBuf buffer) {
        NbtList list = new NbtList();
        byte id = GenericNbtAdapter.NBT_ID.readBytes(buffer).orElseThrow();
        int size = Adapters.INT_SEGMENTED_3.readBytes(buffer).orElseThrow();

        for(int i = 0; i < size; i++) {
            list.add((NbtElement)Adapters.NBT[id].readBytes(buffer).orElseThrow());
        }

        return list;
    }

    @Override
    protected void writeTagData(NbtList value, DataOutput data) throws IOException {
        GenericNbtAdapter.NBT_ID.writeData(value.getHeldType(), data);
        Adapters.INT_SEGMENTED_3.writeData(value.size(), data);

        for(NbtElement element : value) {
            Adapters.NBT[value.getHeldType()].writeData(element, data);
        }
    }

    @Override
    protected NbtList readTagData(DataInput data) throws IOException {
        NbtList list = new NbtList();
        byte id = GenericNbtAdapter.NBT_ID.readData(data).orElseThrow();
        int size = Adapters.INT_SEGMENTED_3.readData(data).orElseThrow();

        for(int i = 0; i < size; i++) {
            list.add((NbtElement) Adapters.NBT[id].readData(data).orElseThrow());
        }

        return list;
    }

    @Override
    protected NbtElement writeTagNbt(NbtList value) {
        return value.copy();
    }

    
    @Override
    protected NbtList readTagNbt(NbtElement nbt) {
        return nbt instanceof NbtList tag ? tag.copy() : null;
    }

    @Override
    protected JsonElement writeTagJson(NbtList value) {
        JsonArray array = new JsonArray();

        if(!value.stream().allMatch(element -> element instanceof NbtCompound)) {
            array.add(KEY_TO_ID.getInt(value.getHeldType()));
        }

        for(NbtElement tag : value) {
            Adapters.NBT[value.getHeldType()].writeJson(tag).ifPresent(o -> array.add((JsonElement)o));
        }

        return array;
    }

    
    @Override
    protected NbtList readTagJson(JsonElement json) {
        if(json instanceof JsonArray array && !array.isEmpty()) {
            NbtList list = new NbtList();
            int id;

            if(array.get(0) instanceof JsonPrimitive primitive && primitive.isString()
                && (id = KEY_TO_ID.getInt(primitive.getAsString())) >= 0) {
                for(int i = 1; i < array.size(); i++) {
                    Adapters.NBT[id].readJson(array.get(i)).ifPresent(tag -> list.add((NbtElement)tag));
                }
            } else {
                for(int i = 0; i < array.size(); i++) {
                    JsonElement element = array.get(i);

                    try {
                        Adapters.GENERIC_NBT.readJson(element).ifPresent(list::add);
                    } catch(UnsupportedOperationException exception) {
                        return null;
                    }
                }
            }

            return list;
        }

        return null;
    }

}
