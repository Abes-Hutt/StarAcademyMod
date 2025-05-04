package abeshutt.staracademy.data.adapter;

import abeshutt.staracademy.data.bit.ArrayBitBuffer;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.encoding.VarInts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

public interface ISimpleAdapter<T, N extends NbtElement, J extends JsonElement> extends IAdapter<T, N, J, Object> {

    default void writeBits(T value, BitBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> readBits(BitBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    default void writeBytes(T value, ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> readBytes(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    default void writeData(T value, DataOutput data) throws IOException {
        throw new UnsupportedOperationException();
    }

    default Optional<T> readData(DataInput data) throws IOException {
        throw new UnsupportedOperationException();
    }

    default Optional<N> writeNbt(T value) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> readNbt(N nbt) {
        throw new UnsupportedOperationException();
    }

    default Optional<J> writeJson(T value) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> readJson(J json) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void writeBits(T value, BitBuffer buffer, Object context) {
        this.writeBits(value, buffer);
    }

    @Override
    default Optional<T> readBits(BitBuffer buffer, Object context) {
        return this.readBits(buffer);
    }

    @Override
    default void writeBytes(T value, ByteBuf buffer, Object context) {
        this.writeBytes(value, buffer);
    }

    @Override
    default Optional<T> readBytes(ByteBuf buffer, Object context) {
        return this.readBytes(buffer);
    }

    @Override
    default void writeData(T value, DataOutput data, Object context) throws IOException {
        this.writeData(value, data);
    }

    @Override
    default Optional<T> readData(DataInput data, Object context) throws IOException {
        return this.readData(data);
    }

    @Override
    default Optional<N> writeNbt(T value, Object context) {
        return this.writeNbt(value);
    }

    @Override
    default Optional<T> readNbt(N nbt, Object context) {
        return this.readNbt(nbt);
    }

    @Override
    default Optional<J> writeJson(T value, Object context) {
        return this.writeJson(value);
    }

    @Override
    default Optional<T> readJson(J json, Object context) {
        return this.readJson(json);
    }

    @Override
    default JsonElement serialize(T value, Type source, JsonSerializationContext context) {
        return this.writeJson(value, null).map(json -> (JsonElement)json).orElse(JsonNull.INSTANCE);
    }

    @Override
    default T deserialize(JsonElement json, Type source, JsonDeserializationContext context) {
        return this.readJson((J)json, null).orElse(null);
    }

    @Override
    default void encode(ByteBuf buf, T value) {
        ArrayBitBuffer buffer = ArrayBitBuffer.empty();
        this.writeBits(value, buffer);
        long[] serialized = buffer.toLongArray();
        VarInts.write(buf, serialized.length);

        for(long l : serialized) {
           buf.writeLong(l);
        }
    }

    @Override
    default T decode(ByteBuf buf) {
        int size = VarInts.read(buf);
        long[] serialized = new long[size];

        for(int i = 0; i < size; i++) {
           serialized[i] = buf.readLong();
        }

        ArrayBitBuffer buffer = ArrayBitBuffer.backing(serialized, 0);
        return this.readBits(buffer).orElse(null);
    }

    default Codec<T> codecNbt() {
        return new Codec<>() {
            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                try {
                    T1 result = NbtOps.INSTANCE.convertTo(ops, ISimpleAdapter.this.writeNbt(input).orElse(null));
                    return DataResult.success(result);
                } catch(Exception e) {
                    return DataResult.error(e::getMessage);
                }
            }

            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                NbtElement result = ops.convertTo(NbtOps.INSTANCE, input);

                try {
                    return DataResult.success(new Pair<>(
                            ISimpleAdapter.this.readNbt((N)result).orElse(null), input));
                } catch(Exception e) {
                    return DataResult.error(e::getMessage);
                }
            }
        };
    }

    default Codec<T> codecJson() {
        return new Codec<>() {
            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                try {
                    T1 result = JsonOps.INSTANCE.convertTo(ops, ISimpleAdapter.this.writeJson(input).orElse(null));
                    return DataResult.success(result);
                } catch(Exception e) {
                    return DataResult.error(e::getMessage);
                }
            }

            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                JsonElement result = ops.convertTo(JsonOps.INSTANCE, input);

                try {
                    return DataResult.success(new Pair<>(
                            ISimpleAdapter.this.readJson((J)result).orElse(null), input));
                } catch(Exception e) {
                    return DataResult.error(e::getMessage);
                }
            }
        };
    }


}

