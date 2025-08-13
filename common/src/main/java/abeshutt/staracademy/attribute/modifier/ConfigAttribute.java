package abeshutt.staracademy.attribute.modifier;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.type.AttributeType;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public abstract class ConfigAttribute<T, C extends ConfigAttribute.Config> extends Attribute<T> {

    private C config;
    private boolean populated;

    protected ConfigAttribute(AttributeType<T> type, C config) {
        super(type);
        this.config = config;
    }

    public C getConfig() {
        return this.config;
    }

    public boolean isPopulated() {
        return this.populated;
    }

    public void populate(RandomSource random) {
        if(!this.populated) {
            this.generate(random);
            this.populated = true;
        }
    }

    protected abstract void generate(RandomSource random);

    public static class Config implements ISerializable<NbtCompound, JsonObject> {
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

}
