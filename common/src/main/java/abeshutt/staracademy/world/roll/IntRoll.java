package abeshutt.staracademy.world.roll;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import java.util.Optional;

public interface IntRoll extends ISerializable<NbtCompound, JsonObject> {

    int get(RandomSource random);

    static Constant ofConstant(int count) {
        return new Constant(count);
    }

    static Uniform ofUniform(int min, int max) {
        return new Uniform(min, max);
    }

    class Constant implements IntRoll {
        private int count;

        protected Constant() {

        }

        protected Constant(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }

        @Override
        public int get(RandomSource random) {
            return this.count;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.INT_SEGMENTED_7.writeBits(this.count, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            Adapters.INT_SEGMENTED_7.readBits(buffer).ifPresent(value -> this.count = value);
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            NbtCompound nbt = new NbtCompound();
            Adapters.INT.writeNbt(this.count).ifPresent(tag -> nbt.put("count", tag));
            return Optional.of(nbt);
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            Adapters.INT.readNbt(nbt.get("count")).ifPresent(value -> this.count = value);
        }

        @Override
        public Optional<JsonObject> writeJson() {
            JsonObject json = new JsonObject();
            Adapters.INT.writeJson(this.count).ifPresent(tag -> json.add("count", tag));
            return Optional.of(json);
        }

        @Override
        public void readJson(JsonObject json) {
            Adapters.INT.readJson(json.get("count")).ifPresent(value -> this.count = value);
        }
    }

    class Uniform implements IntRoll {
        private int min, max;

        protected Uniform() {

        }

        protected Uniform(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        @Override
        public int get(RandomSource random) {
            return random.nextInt(this.max - this.min + 1) + this.min;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.INT_SEGMENTED_7.writeBits(this.min, buffer);
            Adapters.INT_SEGMENTED_7.writeBits(this.max, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            Adapters.INT_SEGMENTED_7.readBits(buffer).ifPresent(value -> this.min = value);
            Adapters.INT_SEGMENTED_7.readBits(buffer).ifPresent(value -> this.max = value);
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            NbtCompound nbt = new NbtCompound();
            Adapters.INT.writeNbt(this.min).ifPresent(tag -> nbt.put("min", tag));
            Adapters.INT.writeNbt(this.max).ifPresent(tag -> nbt.put("max", tag));
            return Optional.of(nbt);
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            Adapters.INT.readNbt(nbt.get("min")).ifPresent(value -> this.min = value);
            Adapters.INT.readNbt(nbt.get("max")).ifPresent(value -> this.max = value);
        }

        @Override
        public Optional<JsonObject> writeJson() {
            JsonObject json = new JsonObject();
            Adapters.INT.writeJson(this.min).ifPresent(tag -> json.add("min", tag));
            Adapters.INT.writeJson(this.max).ifPresent(tag -> json.add("max", tag));
            return Optional.of(json);
        }

        @Override
        public void readJson(JsonObject json) {
            Adapters.INT.readJson(json.get("min")).ifPresent(value -> this.min = value);
            Adapters.INT.readJson(json.get("max")).ifPresent(value -> this.max = value);
        }
    }

    static int getMin(IntRoll roll) {
        if(roll instanceof Constant constant) {
            return constant.getCount();
        } else if(roll instanceof Uniform uniform) {
            return uniform.getMin();
        }

        throw new UnsupportedOperationException();
    }

    static int getMax(IntRoll roll) {
        if(roll instanceof Constant constant) {
            return constant.getCount();
        } else if(roll instanceof Uniform uniform) {
            return uniform.getMax();
        }

        throw new UnsupportedOperationException();
    }

    class Adapter extends TypeSupplierAdapter<IntRoll> {
        public Adapter() {
            super("type", true);
            this.register("constant", Constant.class, Constant::new);
            this.register("uniform", Uniform.class, Uniform::new);
        }

        @Override
        protected IntRoll readSuppliedNbt(NbtElement nbt) {
            if(nbt instanceof AbstractNbtNumber || nbt instanceof NbtString) {
                Optional<Integer> result = Adapters.INT.readNbt(nbt);
                if(result.isPresent()) return IntRoll.ofConstant(result.get());
            }

            return super.readSuppliedNbt(nbt);
        }

        @Override
        protected IntRoll readSuppliedJson(JsonElement json) {
            if(json instanceof JsonPrimitive primitive && (primitive.isNumber() || primitive.isString())) {
                Optional<Integer> result = Adapters.INT.readJson(json);
                if(result.isPresent()) return IntRoll.ofConstant(result.get());
            }

            return super.readSuppliedJson(json);
        }
    }

}
