package abeshutt.staracademy.world.roll;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;
import java.util.function.Function;

public abstract class NumberRoll implements ISerializable<NbtCompound, JsonObject> {

    public abstract Rational get(RandomSource random);

    public static NumberRoll constant(Number value) {
        return ConstantNumberRoll.of(Rational.of(value));
    }

    public static NumberRoll constant(Number numerator, Number denominator) {
        return constant(Rational.of(numerator, denominator));
    }

    public static NumberRoll uniformII(long min, long max) {
        return uniformResolutionII(min, max, 1);
    }

    public static NumberRoll uniformIE(long min, long max) {
        return uniformResolutionIE(min, max, 1);
    }

    public static NumberRoll uniformEI(long min, long max) {
        return uniformResolutionEI(min, max, 1);
    }

    public static NumberRoll uniformEE(long min, long max) {
        return uniformResolutionEE(min, max, 1);
    }

    public static NumberRoll uniformII(float min, float max) {
        return uniformResolutionII(min, max, Rational.FLOAT_EPSILON);
    }

    public static NumberRoll uniformIE(float min, float max) {
        return uniformResolutionIE(min, max, Rational.FLOAT_EPSILON);
    }

    public static NumberRoll uniformEI(float min, float max) {
        return uniformResolutionEI(min, max, Rational.FLOAT_EPSILON);
    }

    public static NumberRoll uniformEE(float min, float max) {
        return uniformResolutionEE(min, max, Rational.FLOAT_EPSILON);
    }

    public static NumberRoll uniformII(double min, double max) {
        return uniformResolutionII(min, max, Rational.DOUBLE_EPSILON);
    }

    public static NumberRoll uniformIE(double min, double max) {
        return uniformResolutionIE(min, max, Rational.DOUBLE_EPSILON);
    }

    public static NumberRoll uniformEI(double min, double max) {
        return uniformResolutionEI(min, max, Rational.DOUBLE_EPSILON);
    }

    public static NumberRoll uniformEE(double min, double max) {
        return uniformResolutionEE(min, max, Rational.DOUBLE_EPSILON);
    }

    public static NumberRoll uniformResolutionII(Number min, Number max, Number resolution) {
        return uniformResolution(min, max, resolution, true, true);
    }

    public static NumberRoll uniformResolutionIE(Number min, Number max, Number resolution) {
        return uniformResolution(min, max, resolution, true, false);
    }

    public static NumberRoll uniformResolutionEI(Number min, Number max, Number resolution) {
        return uniformResolution(min, max, resolution, false, true);
    }

    public static NumberRoll uniformResolutionEE(Number min, Number max, Number resolution) {
        return uniformResolution(min, max, resolution, false, false);
    }

    public static NumberRoll uniformResolution(Number min, Number max, Number resolution, boolean minInclusive, boolean maxInclusive) {
        return UniformNumberRoll.of(constant(min), constant(max),
                constant(resolution), null,
                minInclusive, maxInclusive);
    }

    public static NumberRoll uniformGranularityII(Number min, Number max, Number resolution) {
        return uniformGranularity(min, max, resolution, true, true);
    }

    public static NumberRoll uniformGranularityIE(Number min, Number max, Number resolution) {
        return uniformGranularity(min, max, resolution, true, false);
    }

    public static NumberRoll uniformGranularityEI(Number min, Number max, Number resolution) {
        return uniformGranularity(min, max, resolution, false, true);
    }

    public static NumberRoll uniformGranularityEE(Number min, Number max, Number resolution) {
        return uniformGranularity(min, max, resolution, false, false);
    }

    public static NumberRoll uniformGranularity(Number min, Number max, Number granularity, boolean minInclusive, boolean maxInclusive) {
        return UniformNumberRoll.of(constant(min), constant(max),
                null, constant(granularity),
                minInclusive, maxInclusive);
    }

    public static class Adapter implements ISimpleAdapter<NumberRoll, NbtElement, JsonElement> {
        private final boolean nullable;

        public Adapter(boolean nullable) {
            this.nullable = nullable;
        }

        public Adapter asNullable() {
            return new Adapter(true);
        }

        @Override
        public void writeBits(NumberRoll value, BitBuffer buffer) {
            if(this.nullable) {
                Adapters.BOOLEAN.writeBits(value == null, buffer);
            }

            if(value != null) {
                switch(value) {
                    case ConstantNumberRoll constant -> {
                        Adapters.BYTE.writeBits((byte)0, buffer);
                        ConstantNumberRoll.Adapter.INSTANCE.writeBits(constant, buffer);
                    }
                    case UniformNumberRoll uniform -> {
                        Adapters.BYTE.writeBits((byte)1, buffer);
                        UniformNumberRoll.Adapter.INSTANCE.writeBits(uniform, buffer);
                    }
                    case TrapezoidalNumberRoll trapezoid -> {
                        Adapters.BYTE.writeBits((byte)2, buffer);
                        TrapezoidalNumberRoll.Adapter.INSTANCE.writeBits(trapezoid, buffer);
                    }
                    default -> throw new UnsupportedOperationException();
                }
            }
        }

        @Override
        public Optional<NumberRoll> readBits(BitBuffer buffer) {
            if(this.nullable && Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                return Optional.empty();
            }

            return Optional.of(switch(Adapters.BYTE.readBits(buffer).orElseThrow()) {
                case 0 -> ConstantNumberRoll.Adapter.INSTANCE.readBits(buffer).orElseThrow();
                case 1 -> UniformNumberRoll.Adapter.INSTANCE.readBits(buffer).orElseThrow();
                case 2 -> TrapezoidalNumberRoll.Adapter.INSTANCE.readBits(buffer).orElseThrow();
                default -> throw new UnsupportedOperationException();
            });
        }

        @Override
        public Optional<NbtElement> writeNbt(NumberRoll value) {
            return switch(value) {
                case null -> Optional.empty();
                case ConstantNumberRoll constant ->
                        ConstantNumberRoll.Adapter.INSTANCE.writeNbt(constant).map(json -> {
                            if(json instanceof NbtCompound object) {
                                object.putString("type", "constant");
                            }

                            return json;
                        });
                case UniformNumberRoll uniform ->
                        UniformNumberRoll.Adapter.INSTANCE.writeNbt(uniform).map(json -> {
                            json.putString("type", "uniform");
                            return json;
                        });
                case TrapezoidalNumberRoll trapezoid ->
                        TrapezoidalNumberRoll.Adapter.INSTANCE.writeNbt(trapezoid).map(json -> {
                            if(json.contains("mode")) {
                                json.putString("type", "triangular");
                            } else {
                                json.putString("type", "trapezoidal");
                            }

                            return json;
                        });
                default -> throw new UnsupportedOperationException();
            };
        }

        @Override
        public Optional<NumberRoll> readNbt(NbtElement nbt) {
            if(nbt instanceof NbtCompound object) {
                return (switch(object.getString("type")) {
                    case "constant" -> ConstantNumberRoll.Adapter.INSTANCE.readNbt(object);
                    case "uniform" -> UniformNumberRoll.Adapter.INSTANCE.readNbt(object);
                    case "triangular", "trapezoidal" -> TrapezoidalNumberRoll.Adapter.INSTANCE.readNbt(object);
                    default -> throw new UnsupportedOperationException();
                }).map(Function.identity());
            }

            return ConstantNumberRoll.Adapter.INSTANCE.readNbt(nbt).map(Function.identity());
        }

        @Override
        public Optional<JsonElement> writeJson(NumberRoll value) {
            return switch(value) {
                case null -> Optional.empty();
                case ConstantNumberRoll constant ->
                        ConstantNumberRoll.Adapter.INSTANCE.writeJson(constant).map(json -> {
                            if(json instanceof JsonObject object) {
                                object.addProperty("type", "constant");
                            }

                            return json;
                        });
                case UniformNumberRoll uniform ->
                        UniformNumberRoll.Adapter.INSTANCE.writeJson(uniform).map(json -> {
                            json.addProperty("type", "uniform");
                            return json;
                        });
                case TrapezoidalNumberRoll trapezoid ->
                        TrapezoidalNumberRoll.Adapter.INSTANCE.writeJson(trapezoid).map(json -> {
                            if(json.has("mode")) {
                                json.addProperty("type", "triangular");
                            } else {
                                json.addProperty("type", "trapezoidal");
                            }

                            return json;
                        });
                default -> throw new UnsupportedOperationException();
            };
        }

        @Override
        public Optional<NumberRoll> readJson(JsonElement json) {
            if(json instanceof JsonObject object) {
                return (switch(object.get("type").getAsString()) {
                    case "constant" -> ConstantNumberRoll.Adapter.INSTANCE.readJson(object);
                    case "uniform" -> UniformNumberRoll.Adapter.INSTANCE.readJson(object);
                    case "triangular", "trapezoidal" -> TrapezoidalNumberRoll.Adapter.INSTANCE.readJson(object);
                    default -> throw new UnsupportedOperationException();
                }).map(Function.identity());
            }

            return ConstantNumberRoll.Adapter.INSTANCE.readJson(json).map(Function.identity());
        }

    }

}
