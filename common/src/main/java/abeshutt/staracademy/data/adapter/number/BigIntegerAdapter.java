package abeshutt.staracademy.data.adapter.number;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerAdapter extends NumberAdapter<BigInteger> {

    public BigIntegerAdapter(boolean nullable) {
        super(nullable);
    }

    public BigIntegerAdapter asNullable() {
        return new BigIntegerAdapter(true);
    }

    @Override
    protected void writeNumberBits(BigInteger value, BitBuffer buffer) {
        Adapters.BYTE_ARRAY.writeBits(value.toByteArray(), buffer);
    }

    @Override
    protected BigInteger readNumberBits(BitBuffer buffer) {
        return new BigInteger(Adapters.BYTE_ARRAY.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeNumberBytes(BigInteger value, ByteBuf buffer) {
        Adapters.BYTE_ARRAY.writeBytes(value.toByteArray(), buffer);
    }

    @Override
    protected BigInteger readNumberBytes(ByteBuf buffer) {
        return new BigInteger(Adapters.BYTE_ARRAY.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeNumberData(BigInteger value, DataOutput data) throws IOException {
        Adapters.BYTE_ARRAY.writeData(value.toByteArray(), data);
    }

    @Override
    protected BigInteger readNumberData(DataInput data) throws IOException {
        return new BigInteger(Adapters.BYTE_ARRAY.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeNumberNbt(BigInteger value) {
        return wrap(reduce(value));
    }
    
    @Override
    protected BigInteger readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return BigInteger.valueOf(numeric.longValue());
        } else if(nbt instanceof NbtByteArray byteArray) {
            return new BigInteger(byteArray.getByteArray());
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.get(0));
        } else if(nbt instanceof NbtString string) {
            return parse(string.asString()).map(number -> {
                return number instanceof BigInteger value ? value : BigInteger.valueOf(number.longValue());
            }).orElse(null);
        }

        return null;
    }

    @Override
    protected JsonElement writeNumberJson(BigInteger value) {
        return new JsonPrimitive(value);
    }

    
    @Override
    protected BigInteger readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsBigInteger();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(number -> {
                    return number instanceof BigInteger value ? value : BigInteger.valueOf(number.longValue());
                }).orElse(null);
            }
        }

        return null;
    }

}
