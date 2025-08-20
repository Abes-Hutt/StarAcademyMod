package abeshutt.staracademy.data.adapter.nbt;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.number.BoundedIntAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class CollectionNbtAdapter extends NbtAdapter<AbstractNbtList<?>> {

    private static NbtAdapter[] ADAPTERS = new NbtAdapter[] {
        Adapters.BYTE_ARRAY_NBT,
        Adapters.INT_ARRAY_NBT,
        Adapters.LONG_ARRAY_NBT,
        Adapters.LIST_NBT
    };

    private static final BoundedIntAdapter ID = new BoundedIntAdapter(0, ADAPTERS.length - 1, false);
    private static final Object2IntMap<Class<?>> TYPE_TO_ID = new Object2IntOpenHashMap<>();

    public CollectionNbtAdapter(boolean nullable) {
        super(nullable);
    }

    public CollectionNbtAdapter asNullable() {
        return new CollectionNbtAdapter(this.isNullable());
    }

    @Override
    protected void writeTagBits(AbstractNbtList<?> value, BitBuffer buffer) {
        int id = TYPE_TO_ID.getInt(value.getClass());
        ID.writeBits(id, buffer);
        ADAPTERS[id].writeBits(value, buffer);
    }

    @Override
    protected AbstractNbtList<?> readTagBits(BitBuffer buffer) {
        int id = ID.readBits(buffer).orElseThrow();
        return (AbstractNbtList<?>)ADAPTERS[id].readBits(buffer).orElse(null);
    }

    @Override
    protected void writeTagBytes(AbstractNbtList<?> value, ByteBuf buffer) {
        int id = TYPE_TO_ID.getInt(value.getClass());
        ID.writeBytes(id, buffer);
        ADAPTERS[id].writeBytes(value, buffer);
    }

    @Override
    protected AbstractNbtList<?> readTagBytes(ByteBuf buffer) {
        int id = ID.readBytes(buffer).orElseThrow();
        return (AbstractNbtList<?>)ADAPTERS[id].readBytes(buffer).orElse(null);
    }

    @Override
    protected void writeTagData(AbstractNbtList<?> value, DataOutput data) throws IOException {
        int id = TYPE_TO_ID.getInt(value.getClass());
        ID.writeData(id, data);
        ADAPTERS[id].writeData(value, data);
    }

    @Override
    protected AbstractNbtList<?> readTagData(DataInput data) throws IOException {
        int id = ID.readData(data).orElseThrow();
        return (AbstractNbtList<?>)ADAPTERS[id].readData(data).orElse(null);
    }

    @Override
    protected NbtElement writeTagNbt(AbstractNbtList<?> value) {
        return value;
    }
    
    @Override
    protected AbstractNbtList<?> readTagNbt(NbtElement nbt) {
        return nbt instanceof AbstractNbtList<?> tag ? tag : null;
    }

    @Override
    protected JsonElement writeTagJson(AbstractNbtList<?> value) {
        int id = TYPE_TO_ID.getInt(value.getClass());
        return (JsonElement)ADAPTERS[id].writeJson(value).orElse(null);
    }

    
    @Override
    protected AbstractNbtList<?> readTagJson(JsonElement json) {
        if(json instanceof JsonArray array && !array.isEmpty()) {
            String key = array.get(0) instanceof JsonPrimitive primitive && primitive.isString() ? primitive.getAsString() : null;

            if("B".equals(key)) {
                return Adapters.BYTE_ARRAY_NBT.readJson(json).orElse(null);
            } else if("I".equals(key)) {
                return Adapters.INT_ARRAY_NBT.readJson(json).orElse(null);
            } else if("L".equals(key)) {
                return Adapters.LONG_ARRAY_NBT.readJson(json).orElse(null);
            }
        }

        return Adapters.LIST_NBT.readJson(json).orElse(null);
    }

}
