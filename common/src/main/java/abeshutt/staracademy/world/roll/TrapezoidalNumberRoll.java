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

import java.util.Optional;

public class TrapezoidalNumberRoll extends NumberRoll {

    public final NumberRoll minimum;
    public final NumberRoll maximum;
    public final NumberRoll modeMinimum;
    public final NumberRoll modeMaximum;
    public final NumberRoll resolution;
    public final NumberRoll granularity;
    public final NumberRoll precision;
    public final boolean minimumInclusive;
    public final boolean maximumInclusive;

    protected TrapezoidalNumberRoll(NumberRoll minimum, NumberRoll maximum, NumberRoll modeMinimum, NumberRoll modeMaximum,
                                    NumberRoll resolution, NumberRoll granularity, NumberRoll precision,
                                    boolean minimumInclusive, boolean maximumInclusive) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.modeMinimum = modeMinimum;
        this.modeMaximum = modeMaximum;
        this.resolution = resolution;
        this.granularity = granularity;
        this.precision = precision;
        this.minimumInclusive = minimumInclusive;
        this.maximumInclusive = maximumInclusive;
    }

    public static TrapezoidalNumberRoll of(NumberRoll minimum, NumberRoll maximum, NumberRoll modeMinimum, NumberRoll modeMaximum,
                                           NumberRoll resolution, NumberRoll granularity, NumberRoll precision,
                                           boolean minimumInclusive, boolean maximumInclusive) {
        return new TrapezoidalNumberRoll(minimum, maximum, modeMinimum, modeMaximum, resolution, granularity, precision, minimumInclusive, maximumInclusive);
    }

    @Override
    public Rational get(RandomSource random) {
        Rational minimum = this.minimum.get(random);
        Rational maximum = this.maximum.get(random);
        Rational precision = this.precision.get(random);
        Rational resolution = this.resolution != null ? this.resolution.get(random) : null;
        Rational granularity = this.granularity != null ? this.granularity.get(random) : null;
        Rational modeMinimum = this.modeMinimum.get(random);
        Rational modeMaximum = this.modeMaximum.get(random);

        if(this.resolution != null) {
            granularity = maximum.subtract(minimum).divide(resolution);
        } else if(this.granularity != null) {
            resolution = maximum.subtract(minimum).divide(granularity);
        } else {
            throw new UnsupportedOperationException();
        }

        Rational sample = UniformNumberRoll.of(
                NumberRoll.constant(minimum),
                NumberRoll.constant(maximum),
                NumberRoll.constant(resolution.divide(precision)),
                NumberRoll.constant(granularity.multiply(precision)),
                this.minimumInclusive,
                this.maximumInclusive
        ).get(random);

        Rational precise;

        Rational areaLeft = modeMinimum.subtract(minimum);
        Rational areaCenter = modeMaximum.subtract(modeMinimum).multiply(Rational.TWO);
        Rational areaRight = maximum.subtract(modeMaximum);
        Rational areaTotal = areaLeft.add(areaCenter).add(areaRight);
        sample = sample.subtract(minimum)
                .divide(maximum.subtract(minimum))
                .multiply(areaTotal);

        if(sample.compareTo(areaLeft) < 0) {
            NumberRoll left = UniformNumberRoll.of(
                    NumberRoll.constant(minimum),
                    NumberRoll.constant(modeMinimum),
                    NumberRoll.constant(resolution),
                    NumberRoll.constant(granularity),
                    this.minimumInclusive,
                    false);

            precise = left.get(random).max(left.get(random));
        } else if(areaTotal.subtract(sample).compareTo(areaRight) < 0) {
            NumberRoll right = UniformNumberRoll.of(
                    NumberRoll.constant(modeMaximum),
                    NumberRoll.constant(maximum),
                    NumberRoll.constant(resolution),
                    NumberRoll.constant(granularity),
                    false,
                    this.maximumInclusive);

            precise = right.get(random).min(right.get(random));
        } else {
            NumberRoll center = UniformNumberRoll.of(
                    NumberRoll.constant(modeMaximum),
                    NumberRoll.constant(maximum),
                    NumberRoll.constant(resolution),
                    NumberRoll.constant(granularity),
                    true,
                    true);

            precise = center.get(random);
        }

        Rational scalar = precise.subtract(minimum).divide(resolution).round();
        return minimum.add(resolution.multiply(scalar));
    }

    protected static class Adapter implements ISimpleAdapter<TrapezoidalNumberRoll, NbtCompound, JsonObject> {
        public static final Adapter INSTANCE = new Adapter();

        @Override
        public void writeBits(TrapezoidalNumberRoll value, BitBuffer buffer) {
            Adapters.NUMBER_ROLL.writeBits(value.minimum, buffer);
            Adapters.NUMBER_ROLL.writeBits(value.maximum, buffer);
            Adapters.NUMBER_ROLL.writeBits(value.modeMinimum, buffer);
            Adapters.NUMBER_ROLL.writeBits(value.modeMaximum, buffer);
            Adapters.NUMBER_ROLL.asNullable().writeBits(value.resolution, buffer);
            Adapters.NUMBER_ROLL.asNullable().writeBits(value.granularity, buffer);
            Adapters.NUMBER_ROLL.writeBits(value.precision, buffer);
            Adapters.BOOLEAN.writeBits(value.minimumInclusive, buffer);
            Adapters.BOOLEAN.writeBits(value.maximumInclusive, buffer);
        }

        @Override
        public Optional<TrapezoidalNumberRoll> readBits(BitBuffer buffer) {
            return Optional.of(TrapezoidalNumberRoll.of(
                    Adapters.NUMBER_ROLL.readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.asNullable().readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.asNullable().readBits(buffer).orElseThrow(),
                    Adapters.NUMBER_ROLL.readBits(buffer).orElseThrow(),
                    Adapters.BOOLEAN.readBits(buffer).orElseThrow(),
                    Adapters.BOOLEAN.readBits(buffer).orElseThrow()
            ));
        }

        @Override
        public Optional<NbtCompound> writeNbt(TrapezoidalNumberRoll value) {
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

                if(value.modeMinimum.equals(value.modeMaximum)) {
                    Adapters.NUMBER_ROLL.writeNbt(value.modeMinimum).ifPresent(tag -> {
                        nbt.put("mode", tag);
                    });
                } else {
                    Adapters.NUMBER_ROLL.writeNbt(value.modeMinimum).ifPresent(tag -> {
                        nbt.put("mode_minimum", tag);
                    });

                    Adapters.NUMBER_ROLL.writeNbt(value.modeMaximum).ifPresent(tag -> {
                        nbt.put("mode_maximum", tag);
                    });
                }

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

                if(!(value.precision instanceof ConstantNumberRoll precision) || !precision.value.equals(Rational.of(10))) {
                    Adapters.NUMBER_ROLL.writeNbt(value.precision).ifPresent(tag -> {
                        nbt.put("precision", tag);
                    });
                }

                return nbt;
            });
        }

        @Override
        public Optional<TrapezoidalNumberRoll> readNbt(NbtCompound nbt) {
            NumberRoll minimum;
            NumberRoll maximum;
            NumberRoll modeMinimum;
            NumberRoll modeMaximum;
            NumberRoll resolution;
            NumberRoll granularity;
            NumberRoll precision;
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

            if(nbt.contains("mode")) {
                if(nbt.contains("mode_minimum") || nbt.contains("mode_maximum")) {
                    throw new RuntimeException("roll already includes 'mode'");
                }

                modeMinimum = Adapters.NUMBER_ROLL.readNbt(nbt.get("mode")).orElseThrow();
                modeMaximum = modeMinimum;
            } else if(!nbt.contains("mode_minimum") || !nbt.contains("mode_maximum")) {
                throw new RuntimeException("roll needs both 'mode_minimum' and 'mode_maximum'");
            } else {
                modeMinimum = Adapters.NUMBER_ROLL.readNbt(nbt.get("mode_minimum")).orElseThrow();
                modeMaximum = Adapters.NUMBER_ROLL.readNbt(nbt.get("mode_maximum")).orElseThrow();
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

            precision = Adapters.NUMBER_ROLL.readNbt(nbt.get("precision")).orElseGet(() -> {
                return NumberRoll.constant(Rational.of(10));
            });

            return Optional.of(TrapezoidalNumberRoll.of(minimum, maximum, modeMinimum, modeMaximum, resolution,
                    granularity, precision, minimumInclusive, maximumInclusive));
        }

        @Override
        public Optional<JsonObject> writeJson(TrapezoidalNumberRoll value) {
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

                if(value.modeMinimum.equals(value.modeMaximum)) {
                    Adapters.NUMBER_ROLL.writeJson(value.modeMinimum).ifPresent(tag -> {
                        json.add("mode", tag);
                    });
                } else {
                    Adapters.NUMBER_ROLL.writeJson(value.modeMinimum).ifPresent(tag -> {
                        json.add("mode_minimum", tag);
                    });

                    Adapters.NUMBER_ROLL.writeJson(value.modeMaximum).ifPresent(tag -> {
                        json.add("mode_maximum", tag);
                    });
                }

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

                if(!(value.precision instanceof ConstantNumberRoll precision) || !precision.value.equals(Rational.of(10))) {
                    Adapters.NUMBER_ROLL.writeJson(value.precision).ifPresent(tag -> {
                        json.add("precision", tag);
                    });
                }

                return json;
            });
        }

        @Override
        public Optional<TrapezoidalNumberRoll> readJson(JsonObject json) {
            NumberRoll minimum;
            NumberRoll maximum;
            NumberRoll modeMinimum;
            NumberRoll modeMaximum;
            NumberRoll resolution;
            NumberRoll granularity;
            NumberRoll precision;
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

            if(json.has("mode")) {
                if(json.has("mode_minimum") || json.has("mode_maximum")) {
                    throw new RuntimeException("roll already includes 'mode'");
                }

                modeMinimum = Adapters.NUMBER_ROLL.readJson(json.get("mode")).orElseThrow();
                modeMaximum = modeMinimum;
            } else if(!json.has("mode_minimum") || !json.has("mode_maximum")) {
                throw new RuntimeException("roll needs both 'mode_minimum' and 'mode_maximum'");
            } else {
                modeMinimum = Adapters.NUMBER_ROLL.readJson(json.get("mode_minimum")).orElseThrow();
                modeMaximum = Adapters.NUMBER_ROLL.readJson(json.get("mode_maximum")).orElseThrow();
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

            precision = Adapters.NUMBER_ROLL.readJson(json.get("precision")).orElseGet(() -> {
                return NumberRoll.constant(Rational.of(10));
            });

            return Optional.of(TrapezoidalNumberRoll.of(minimum, maximum, modeMinimum, modeMaximum, resolution,
                    granularity, precision, minimumInclusive, maximumInclusive));
        }
    }

}
