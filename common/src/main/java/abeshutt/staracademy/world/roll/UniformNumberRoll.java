package abeshutt.staracademy.world.roll;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;

import java.math.BigInteger;
import java.util.Optional;

public class UniformNumberRoll extends NumberRoll {

    public final NumberRoll minimum;
    public final NumberRoll maximum;
    public final NumberRoll resolution;
    public final NumberRoll granularity;
    public final boolean minimumInclusive;
    public final boolean maximumInclusive;

    protected UniformNumberRoll(NumberRoll minimum, NumberRoll maximum, NumberRoll resolution, NumberRoll granularity,
                                boolean minimumInclusive, boolean maximumInclusive) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.resolution = resolution;
        this.granularity = granularity;
        this.minimumInclusive = minimumInclusive;
        this.maximumInclusive = maximumInclusive;
    }

    public static UniformNumberRoll of(NumberRoll minimum, NumberRoll maximum, NumberRoll resolution, NumberRoll granularity, boolean minimumInclusive, boolean maximumInclusive) {
        return new UniformNumberRoll(minimum, maximum, resolution, granularity, minimumInclusive, maximumInclusive);
    }

    @Override
    public Rational get(RandomSource random) {
        Rational minimum = this.minimum.get(random);
        Rational maximum = this.maximum.get(random);
        Rational resolution;
        Rational granularity;

        if(minimum.equals(maximum)) {
            return minimum;
        }

        if(this.resolution != null && this.granularity != null) {
            resolution = this.resolution.get(random);
            granularity = this.granularity.get(random);
        } else if(this.resolution != null) {
            resolution = this.resolution.get(random);
            granularity = maximum.subtract(minimum).divide(resolution);
        } else if(this.granularity != null) {
            granularity = this.granularity.get(random);
            resolution = maximum.subtract(minimum).divide(granularity);
        } else {
            throw new UnsupportedOperationException();
        }

        Rational bound = granularity.floor();

        if(this.maximumInclusive && bound.equals(granularity)) {
            bound = bound.add(Rational.ONE);
        }

        if(!this.minimumInclusive) {
            bound = bound.subtract(Rational.ONE);
        }

        BigInteger index = random.nextBigInteger(bound.getNumerator());

        if(!this.minimumInclusive) {
            index = index.add(BigInteger.ONE);
        }

        return minimum.add(resolution.multiply(index));
    }

    protected static class Adapter implements ISimpleAdapter<UniformNumberRoll, NbtCompound, JsonObject> {
        public static final Adapter INSTANCE = new Adapter();

        @Override
        public void writeBits(UniformNumberRoll value, BitBuffer buffer) {
            Adapters.NUMBER_ROLL.writeBits(value.minimum, buffer);
            Adapters.NUMBER_ROLL.writeBits(value.maximum, buffer);
            Adapters.NUMBER_ROLL.asNullable().writeBits(value.resolution, buffer);
            Adapters.NUMBER_ROLL.asNullable().writeBits(value.granularity, buffer);
            Adapters.BOOLEAN.writeBits(value.minimumInclusive, buffer);
            Adapters.BOOLEAN.writeBits(value.maximumInclusive, buffer);
        }

        @Override
        public Optional<UniformNumberRoll> readBits(BitBuffer buffer) {
            return Optional.of(UniformNumberRoll.of(
                    Adapters.NUMBER_ROLL.readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.asNullable().readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.asNullable().readBits(buffer).orElseThrow(),
                    Adapters.BOOLEAN.readBits(buffer).orElseThrow(),
                    Adapters.BOOLEAN.readBits(buffer).orElseThrow()
            ));
        }

        @Override
        public Optional<NbtCompound> writeNbt(UniformNumberRoll value) {
            if(value == null) {
                return Optional.empty();
            }

            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.NUMBER_ROLL.writeNbt(value.minimum).ifPresent(tag -> {
                    nbt.put(value.minimumInclusive ? "minimum_inclusive" : "minimum_exclusive", tag);
                });

                Adapters.NUMBER_ROLL.writeNbt(value.maximum).ifPresent(tag -> {
                    nbt.put(value.minimumInclusive ? "maximum_inclusive" : "maximum_exclusive", tag);
                });

                if(value.resolution != null) {
                    Rational number = value.resolution instanceof ConstantNumberRoll constant ? constant.value : null;

                    if(Rational.ONE.equals(number)) {
                        nbt.put("resolution", NbtString.of("integer"));
                    } else if(Rational.FLOAT_EPSILON.equals(number)) {
                        nbt.put("resolution", NbtString.of("float"));
                    } else if(Rational.DOUBLE_EPSILON.equals(number)) {
                        nbt.put("resolution", NbtString.of("double"));
                    } else {
                        Adapters.NUMBER_ROLL.writeNbt(value.resolution).ifPresent(tag -> {
                            nbt.put("resolution", tag);
                        });
                    }
                }

                if(value.granularity != null) {
                    Adapters.NUMBER_ROLL.writeNbt(value.granularity).ifPresent(tag -> {
                        nbt.put("granularity", tag);
                    });
                }

                return nbt;
            });
        }

        @Override
        public Optional<UniformNumberRoll> readNbt(NbtCompound nbt) {
            NumberRoll minimum;
            NumberRoll maximum;
            NumberRoll resolution;
            NumberRoll granularity;
            boolean minimumInclusive;
            boolean maximumInclusive;

            if(nbt.contains("minimum_inclusive") && nbt.contains("minimum_exclusive")) {
                throw new RuntimeException("roll cannot contain both 'minimum_inclusive' and 'minimum_exclusive'");
            } else if(nbt.contains("minimum_inclusive")) {
                minimum = Adapters.NUMBER_ROLL.readNbt(nbt.get("minimum_inclusive")).orElseThrow();
                minimumInclusive = true;
            } else if(nbt.contains("minimum_exclusive")) {
                minimum = Adapters.NUMBER_ROLL.readNbt(nbt.get("minimum_exclusive")).orElseThrow();
                minimumInclusive = false;
            } else {
                throw new RuntimeException("roll needs either 'minimum_inclusive' or 'minimum_exclusive'");
            }

            if(nbt.contains("maximum_inclusive") && nbt.contains("maximum_exclusive")) {
                throw new RuntimeException("roll cannot contain both 'maximum_inclusive' and 'maximum_exclusive'");
            } else if(nbt.contains("maximum_inclusive")) {
                maximum = Adapters.NUMBER_ROLL.readNbt(nbt.get("maximum_inclusive")).orElseThrow();
                maximumInclusive = true;
            } else if(nbt.contains("maximum_exclusive")) {
                maximum = Adapters.NUMBER_ROLL.readNbt(nbt.get("maximum_exclusive")).orElseThrow();
                maximumInclusive = false;
            } else {
                throw new RuntimeException("roll needs either 'maximum_inclusive' or 'maximum_exclusive'");
            }

            if(nbt.contains("resolution") && nbt.contains("granularity")) {
                throw new RuntimeException("roll cannot contain both 'resolution' and 'granularity'");
            } else if(nbt.contains("resolution")) {
                if(nbt.get("resolution") instanceof NbtString primitive) {
                    resolution = switch(primitive.asString()) {
                        case "integer" -> NumberRoll.constant(Rational.ONE);
                        case "float" -> NumberRoll.constant(Rational.FLOAT_EPSILON);
                        case "double" -> NumberRoll.constant(Rational.DOUBLE_EPSILON);
                        default -> Adapters.NUMBER_ROLL.readNbt(primitive).orElseThrow();
                    };
                } else {
                    resolution = Adapters.NUMBER_ROLL.readNbt(nbt.get("resolution")).orElseThrow();
                }

                granularity = null;
            } else if(nbt.contains("granularity")) {
                granularity = Adapters.NUMBER_ROLL.readNbt(nbt.get("granularity")).orElseThrow();
                resolution = null;
            } else {
                throw new RuntimeException("roll needs either 'resolution' or 'granularity'");
            }

            return Optional.of(UniformNumberRoll.of(minimum, maximum, resolution, granularity, minimumInclusive, maximumInclusive));
        }

        @Override
        public Optional<JsonObject> writeJson(UniformNumberRoll value) {
            if(value == null) {
                return Optional.empty();
            }

            return Optional.of(new JsonObject()).map(json -> {
                Adapters.NUMBER_ROLL.writeJson(value.minimum).ifPresent(tag -> {
                    json.add(value.minimumInclusive ? "minimum_inclusive" : "minimum_exclusive", tag);
                });

                Adapters.NUMBER_ROLL.writeJson(value.maximum).ifPresent(tag -> {
                    json.add(value.minimumInclusive ? "maximum_inclusive" : "maximum_exclusive", tag);
                });

                if(value.resolution != null) {
                    Rational number = value.resolution instanceof ConstantNumberRoll constant ? constant.value : null;

                    if(Rational.ONE.equals(number)) {
                        json.addProperty("resolution", "integer");
                    } else if(Rational.FLOAT_EPSILON.equals(number)) {
                        json.addProperty("resolution", "float");
                    } else if(Rational.DOUBLE_EPSILON.equals(number)) {
                        json.addProperty("resolution", "double");
                    } else {
                        Adapters.NUMBER_ROLL.writeJson(value.resolution).ifPresent(tag -> {
                            json.add("resolution", tag);
                        });
                    }
                }

                if(value.granularity != null) {
                    Adapters.NUMBER_ROLL.writeJson(value.granularity).ifPresent(tag -> {
                        json.add("granularity", tag);
                    });
                }

                return json;
            });
        }

        @Override
        public Optional<UniformNumberRoll> readJson(JsonObject json) {
            NumberRoll minimum;
            NumberRoll maximum;
            NumberRoll resolution;
            NumberRoll granularity;
            boolean minimumInclusive;
            boolean maximumInclusive;

            if(json.has("minimum_inclusive") && json.has("minimum_exclusive")) {
                throw new RuntimeException("roll cannot contain both 'minimum_inclusive' and 'minimum_exclusive'");
            } else if(json.has("minimum_inclusive")) {
                minimum = Adapters.NUMBER_ROLL.readJson(json.get("minimum_inclusive")).orElseThrow();
                minimumInclusive = true;
            } else if(json.has("minimum_exclusive")) {
                minimum = Adapters.NUMBER_ROLL.readJson(json.get("minimum_exclusive")).orElseThrow();
                minimumInclusive = false;
            } else {
                throw new RuntimeException("roll needs either 'minimum_inclusive' or 'minimum_exclusive'");
            }

            if(json.has("maximum_inclusive") && json.has("maximum_exclusive")) {
                throw new RuntimeException("roll cannot contain both 'maximum_inclusive' and 'maximum_exclusive'");
            } else if(json.has("maximum_inclusive")) {
                maximum = Adapters.NUMBER_ROLL.readJson(json.get("maximum_inclusive")).orElseThrow();
                maximumInclusive = true;
            } else if(json.has("maximum_exclusive")) {
                maximum = Adapters.NUMBER_ROLL.readJson(json.get("maximum_exclusive")).orElseThrow();
                maximumInclusive = false;
            } else {
                throw new RuntimeException("roll needs either 'maximum_inclusive' or 'maximum_exclusive'");
            }

            if(json.has("resolution") && json.has("granularity")) {
                throw new RuntimeException("roll cannot contain both 'resolution' and 'granularity'");
            } else if(json.has("resolution")) {
                if(json.get("resolution") instanceof JsonPrimitive primitive && primitive.isString()) {
                    resolution = switch(primitive.getAsString()) {
                        case "integer" -> NumberRoll.constant(Rational.ONE);
                        case "float" -> NumberRoll.constant(Rational.FLOAT_EPSILON);
                        case "double" -> NumberRoll.constant(Rational.DOUBLE_EPSILON);
                        default -> Adapters.NUMBER_ROLL.readJson(primitive).orElseThrow();
                    };
                } else {
                    resolution = Adapters.NUMBER_ROLL.readJson(json.get("resolution")).orElseThrow();
                }

                granularity = null;
            } else if(json.has("granularity")) {
                granularity = Adapters.NUMBER_ROLL.readJson(json.get("granularity")).orElseThrow();
                resolution = null;
            } else {
                throw new RuntimeException("roll needs either 'resolution' or 'granularity'");
            }

            return Optional.of(UniformNumberRoll.of(minimum, maximum, resolution, granularity, minimumInclusive, maximumInclusive));
        }
    }

}
