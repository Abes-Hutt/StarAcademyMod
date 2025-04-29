package abeshutt.staracademy.data.adapter;

import abeshutt.staracademy.data.bit.BitBuffer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public interface IBitAdapter<T, C> extends PacketCodec<ByteBuf, T> {

    void writeBits(T value, BitBuffer buffer, C context);

    Optional<T> readBits(BitBuffer buffer, C context);

    @Override
    void encode(ByteBuf buf, T value);

    @Override
    T decode(ByteBuf buf);

}
