package abeshutt.staracademy.data.adapter.basic;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TypeSupplierAdapter<T extends ISerializable<?, ?>> extends SupplierAdapter<T> {

    protected Map<String, Supplier<? extends T>> typeToSupplier = new HashMap<>();
    protected Map<Class<? extends T>, String> classToType = new HashMap<>();
    protected final String key;

    public TypeSupplierAdapter(String key, boolean nullable) {
        super(nullable);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public Set<Class<? extends T>> getClasses() {
        return this.classToType.keySet();
    }

    public <A extends TypeSupplierAdapter<T>> A register(String id, Supplier<? extends T> supplier) {
        return this.register(id, null, supplier);
    }

    public <A extends TypeSupplierAdapter<T>> A register(String id, Class<? extends T> type, Supplier<? extends T> supplier) {
        this.typeToSupplier.put(id, supplier);
        this.classToType.put(type, id);
        return (A)this;
    }

    public String getType(T value) {
        return value == null ? null : this.classToType.get(value.getClass());
    }

    public T getValue(String key) {
        return this.typeToSupplier.containsKey(key) ? this.typeToSupplier.get(key).get() : null;
    }

    @Override
    protected void writeSuppliedBits(T value, BitBuffer buffer) {
        if(this.isNullable()) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.UTF_8.writeBits(this.getType(value), buffer);
            value.writeBits(buffer);
        }
    }

    @Override
    protected T readSuppliedBits(BitBuffer buffer) {
        if(this.isNullable() && buffer.readBoolean()) {
            return null;
        }

        T value = this.getValue(Adapters.UTF_8.readBits(buffer).orElseThrow());
        value.readBits(buffer);
        return value;
    }

    @Override
    protected void writeSuppliedBytes(T value, ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected T readSuppliedBytes(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeSuppliedData(T value, DataOutput data) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected T readSuppliedData(DataInput data) throws IOException {
        throw new UnsupportedOperationException();
    }

    
    @Override
    protected NbtElement writeSuppliedNbt(T value) {
        String type = this.getType(value);
        if(type == null) return null;

        return value.writeNbt().map(nbt -> {
            if(nbt instanceof NbtCompound compound) {
                compound.putString(this.key, this.getType(value));
            }

            return nbt;
        }).orElse(null);
    }

    
    @Override
    protected T readSuppliedNbt(NbtElement nbt) {
        if(!(nbt instanceof NbtCompound compound)) {
            return null;
        }

        T value = this.getValue(compound.getString(this.key));

        if(value != null) {
            ((ISerializable)value).readNbt(nbt);
        } else if(!compound.isEmpty()) {
            StarAcademyMod.LOGGER.error("Could not deserialize " + nbt);
        }

        return value;
    }

    @Override
    protected JsonElement writeSuppliedJson(T value) {
        String type = this.getType(value);
        if(type == null) return null;

        return value.writeJson().map(nbt -> {
            if(nbt instanceof JsonObject object) {
                JsonObject result = new JsonObject();
                result.addProperty(this.key, this.getType(value));

                for (String key : object.keySet()) {
                    result.add(key, object.get(key));
                }

                return result;
            }

            return nbt;
        }).orElse(null);
    }

    
    @Override
    protected T readSuppliedJson(JsonElement json) {
        if(!(json instanceof JsonObject object)) {
            return null;
        }

        T value = this.getValue(object.get(this.key).getAsString());

        if(value != null) {
            ((ISerializable)value).readJson(json);
        } else if(object.keySet().size() != 0) {
            StarAcademyMod.LOGGER.error("Could not deserialize " + json);
        }

        return value;
    }

}
