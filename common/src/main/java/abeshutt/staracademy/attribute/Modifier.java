package abeshutt.staracademy.attribute;

import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public abstract class Modifier<T> implements ISerializable<NbtCompound, JsonObject> {

    public abstract Option<T> apply(Option<T> value);

    @Override
    public void writeBits(BitBuffer buffer) {

    }

    @Override
    public void readBits(BitBuffer buffer) {

    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound());
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject());
    }

    @Override
    public void readJson(JsonObject json) {

    }

}
