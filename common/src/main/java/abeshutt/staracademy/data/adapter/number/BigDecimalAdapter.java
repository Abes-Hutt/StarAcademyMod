package abeshutt.staracademy.data.adapter.number;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalAdapter extends NumberAdapter<BigDecimal> {

    public BigDecimalAdapter(boolean nullable) {
        super(nullable);
    }

    public BigDecimalAdapter asNullable() {
        return new BigDecimalAdapter(true);
    }

    @Override
    protected void writeNumberBits(BigDecimal value, BitBuffer buffer) {
        Adapters.BIG_INTEGER.writeBits(value.unscaledValue(), buffer);
        Adapters.INT.writeBits(value.scale(), buffer);
    }

    @Override
    protected BigDecimal readNumberBits(BitBuffer buffer) {
        return new BigDecimal(
            Adapters.BIG_INTEGER.readBits(buffer).orElseThrow(),
            Adapters.INT.readBits(buffer).orElseThrow());
    }

    @Override
    protected void writeNumberBytes(BigDecimal value, ByteBuf buffer) {
        Adapters.BIG_INTEGER.writeBytes(value.unscaledValue(), buffer);
        Adapters.INT.writeBytes(value.scale(), buffer);
    }

    @Override
    protected BigDecimal readNumberBytes(ByteBuf buffer) {
        return new BigDecimal(
            Adapters.BIG_INTEGER.readBytes(buffer).orElseThrow(),
            Adapters.INT.readBytes(buffer).orElseThrow());
    }

    @Override
    protected void writeNumberData(BigDecimal value, DataOutput data) throws IOException {
        Adapters.BIG_INTEGER.writeData(value.unscaledValue(), data);
        Adapters.INT.writeData(value.scale(), data);
    }

    @Override
    protected BigDecimal readNumberData(DataInput data) throws IOException {
        return new BigDecimal(
            Adapters.BIG_INTEGER.readData(data).orElseThrow(),
            Adapters.INT.readData(data).orElseThrow());
    }

    @Override
    protected NbtElement writeNumberNbt(BigDecimal value) {
        return wrap(reduce(value));
    }
    
    @Override
    protected BigDecimal readNumberNbt(NbtElement nbt) {
        if(nbt instanceof AbstractNbtNumber numeric) {
            return new BigDecimal(numeric.numberValue().toString());
        } else if(nbt instanceof NbtList list && list.size() == 1) {
            return this.readNumberNbt(list.getFirst());
        } else if(nbt instanceof NbtString string) {
            return parse(string.asString()).map(number -> {
                return number instanceof BigDecimal value ? value : new BigDecimal(number.toString());
            }).orElse(null);
        }

        return null;
    }

    @Override
    protected JsonElement writeNumberJson(BigDecimal value) {
        return new JsonPrimitive(value);
    }

    @Override
    protected BigDecimal readNumberJson(JsonElement json) {
        if(json instanceof JsonObject) {
            return null;
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readNumberJson(array.get(0));
        } else if(json instanceof JsonPrimitive primitive) {
            if(primitive.isNumber()) {
                return primitive.getAsBigDecimal();
            } else if(primitive.isString()) {
                return parse(primitive.getAsString()).map(number -> {
                    return number instanceof BigDecimal value ? value : new BigDecimal(number.toString());
                }).orElse(null);
            }
        }

        return null;
    }

}
