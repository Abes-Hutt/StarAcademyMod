package abeshutt.staracademy.world.roll;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonElement;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public class ConstantNumberRoll extends NumberRoll {

    public final Rational value;

    protected ConstantNumberRoll(Rational value) {
        this.value = value;
    }

    public static ConstantNumberRoll of(Number value) {
        return new ConstantNumberRoll(Rational.of(value));
    }

    @Override
    public Rational get(RandomSource random) {
        return this.value;
    }

    public static class Adapter implements ISimpleAdapter<ConstantNumberRoll, NbtElement, JsonElement> {
        public static final Adapter INSTANCE = new Adapter();

        @Override
        public void writeBits(ConstantNumberRoll value, BitBuffer buffer) {
            Adapters.RATIONAL.writeBits(value.value, buffer);
        }

        @Override
        public Optional<ConstantNumberRoll> readBits(BitBuffer buffer) {
            return Optional.of(ConstantNumberRoll.of(Adapters.RATIONAL.readBits(buffer).orElseThrow()));
        }

        @Override
        public Optional<NbtElement> writeNbt(ConstantNumberRoll value) {
            return Adapters.RATIONAL.writeNbt(value.value);
        }

        @Override
        public Optional<ConstantNumberRoll> readNbt(NbtElement nbt) {
            return Adapters.RATIONAL.readNbt(nbt).map(ConstantNumberRoll::of);
        }

        @Override
        public Optional<JsonElement> writeJson(ConstantNumberRoll value) {
            return Adapters.RATIONAL.writeJson(value.value);
        }

        @Override
        public Optional<ConstantNumberRoll> readJson(JsonElement json) {
            return Adapters.RATIONAL.readJson(json).map(ConstantNumberRoll::of);
        }
    }

}
