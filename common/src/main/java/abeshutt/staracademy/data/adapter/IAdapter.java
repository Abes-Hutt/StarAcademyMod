package abeshutt.staracademy.data.adapter;

import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public interface IAdapter<T, N extends NbtElement, J extends JsonElement, C>
    extends IBitAdapter<T, C>, IByteAdapter<T, C>, IDataAdapter<T, C>, INbtAdapter<T, N, C>, IJsonAdapter<T, J, C> {

    @Override
    default void writeBits(T value, BitBuffer buffer, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<T> readBits(BitBuffer buffer, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void writeBytes(T value, ByteBuf buffer, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<T> readBytes(ByteBuf buffer, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void writeData(T value, DataOutput data, C context) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<T> readData(DataInput data, C context) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<N> writeNbt(T value, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<T> readNbt(N nbt, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<J> writeJson(T value, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<T> readJson(J json, C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void encode(ByteBuf buf, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    default T decode(ByteBuf buf) {
        throw new UnsupportedOperationException();
    }

}
