package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.random.RandomSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WeightedList<T> implements ISerializable<NbtElement, JsonElement> {

    private final List<Entry<T>> entries;
    private BigInteger lcm;
    private BigInteger weight;

    protected WeightedList() {
        this.entries = new ArrayList<>();
        this.lcm = BigInteger.ONE;
        this.weight = BigInteger.ZERO;
    }

    public static <T> WeightedList<T> empty() {
        return new WeightedList<>();
    }

    public static <T> WeightedList<T> of(Map<T, Rational> map) {
        return new WeightedList<T>().add(map::forEach);
    }

    public static <T> WeightedList<T> build(Consumer<BiConsumer<T, Rational>> consumer) {
        return new WeightedList<T>().add(consumer);
    }

    public WeightedList<T> add(T value, Rational weight) {
        if(weight.compareTo(Rational.ZERO) <= 0) {
            return this;
        }

        weight.simplify();

        if(!this.lcm.mod(weight.getDenominator()).equals(BigInteger.ZERO)) {
            // Well fuck, new LCM needed.
            BigInteger newLcm = this.lcm.divide(this.lcm.gcd(weight.getDenominator()))
                    .multiply(weight.getDenominator());
            BigInteger scalar = newLcm.divide(this.lcm);

            for(Entry<T> entry : this.entries) {
                entry.weight = entry.weight.multiply(scalar);
            }

            this.lcm = newLcm;
            this.weight = this.weight.multiply(scalar);
        }

        Entry<T> entry = new Entry<>(value, weight.getNumerator()
                .multiply(this.lcm.divide(weight.getDenominator())));
        this.entries.add(entry);
        this.weight = this.weight.add(entry.weight);
        return this;
    }

    public WeightedList<T> addAll(Map<T, Rational> map) {
        map.forEach(this::add);
        return this;
    }

    public WeightedList<T> add(Consumer<BiConsumer<T, Rational>> consumer) {
        List<Pair<T, Rational>> added = new ArrayList<>();

        consumer.accept((value, weight) -> {
            if(weight.compareTo(Rational.ZERO) > 0) {
                added.add(new Pair<>(value, weight.simplify()));
            }
        });

        if(added.isEmpty()) {
            return this;
        } else if(added.size() == 1) {
            return this.add(added.getFirst().getLeft(), added.getFirst().getRight());
        }

        BigInteger total = BigInteger.ONE;

        for(Pair<T, Rational> pair : added) {
            total = total.divide(total.gcd(pair.getRight().getDenominator()))
                    .multiply(pair.getRight().getDenominator());
        }

        if(!this.lcm.mod(total).equals(BigInteger.ZERO)) {
            // Well fuck, new LCM needed.
            BigInteger newLcm = this.lcm.divide(this.lcm.gcd(total)).multiply(total);
            BigInteger scalar = newLcm.divide(this.lcm);

            for(Entry<T> entry : this.entries) {
                entry.weight = entry.weight.multiply(scalar);
            }

            this.lcm = newLcm;
            this.weight = this.weight.multiply(scalar);
        }

        for(Pair<T, Rational> pair : added) {
            Entry<T> entry = new Entry<>(pair.getLeft(), pair.getRight().getNumerator()
                    .multiply(this.lcm.divide(pair.getRight().getDenominator())));
            this.entries.add(entry);
            this.weight = this.weight.add(entry.weight);
        }

        return this;
    }

    public Option<T> getRandom(RandomSource random) {
        if(this.entries.isEmpty()) {
            return Option.absent();
        } else if(this.entries.size() == 1) {
            return Option.present(this.entries.getFirst().value);
        }

        BigInteger index = random.nextBigInteger(this.weight);

        for(Entry<T> entry : this.entries) {
            if(index.compareTo(entry.weight) < 0) {
                return Option.present(entry.value);
            }

            index = index.subtract(entry.weight);
        }

        return Option.absent();
    }

    private static class Entry<T> {
        private final T value;
        private BigInteger weight;

        public Entry(T value, BigInteger weight) {
            this.value = value;
            this.weight = weight;
        }
    }

    public static class Adapter<T> implements ISimpleAdapter<WeightedList<T>, NbtElement, JsonElement> {
        private final IAdapter<T, NbtElement, JsonElement, ?> element;
        private final boolean nullable;

        public Adapter(IAdapter<T, ?, ?, ?> element, boolean nullable) {
            this.element = (IAdapter)element;
            this.nullable = nullable;
        }

        public boolean isNullable() {
            return this.nullable;
        }

        public Adapter<T> asNullable() {
            return new Adapter<>(this.element, this.nullable);
        }

        @Override
        public void writeBits(WeightedList<T> value, BitBuffer buffer) {
            if(this.nullable) {
                Adapters.BOOLEAN.writeBits(value != null, buffer);
            }

            if(value != null) {
                Adapters.INT_SEGMENTED_3.writeBits(value.entries.size(), buffer);

                for(Entry<T> entry : value.entries) {
                    this.element.writeBits(entry.value, buffer, null);
                    Adapters.BIG_INTEGER.writeBits(entry.weight, buffer);
                }
            }
        }

        @Override
        public Optional<WeightedList<T>> readBits(BitBuffer buffer) {
            if(!this.nullable || Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                List<Pair<T, Rational>> entries = new ArrayList<>();
                int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

                for(int i = 0; i < size; i++) {
                   entries.add(new Pair<>(
                       this.element.readBits(buffer, null).orElseThrow(),
                       Rational.of(Adapters.BIG_INTEGER.readBits(buffer).orElseThrow())
                   ));
                }

                return Optional.of(WeightedList.build(consumer -> {
                    entries.forEach(pair -> consumer.accept(pair.getLeft(), pair.getRight()));
                }));
            }

            return Optional.empty();
        }

        @Override
        public Optional<NbtElement> writeNbt(WeightedList<T> value) {
            if(value == null) {
                return Optional.empty();
            }

            List<Pair<NbtElement, NbtElement>> serialized = new ArrayList<>();

            for(Entry<T> entry : value.entries) {
                this.element.writeNbt(entry.value, null).ifPresent(tag -> {
                    serialized.add(new Pair<>(tag, Adapters.RATIONAL.writeNbt(
                            Rational.of(entry.weight, 1), null).orElseThrow()));
                });
            }

            if(serialized.size() == 1) {
                return Optional.of(serialized.getFirst().getLeft());
            }

            NbtList list = new NbtList();

            for(Pair<NbtElement, NbtElement> pair : serialized) {
                if(pair.getLeft() instanceof NbtCompound object && !object.contains("weight")) {
                    object.put("weight", pair.getRight());
                    list.add(pair.getLeft());
                } else {
                    NbtCompound object = new NbtCompound();
                    object.put("value", pair.getLeft());
                    object.put("weight", pair.getRight());
                    list.add(object);
                }
            }

            return Optional.of(list);
        }

        @Override
        public Optional<WeightedList<T>> readNbt(NbtElement nbt) {
            if(nbt == null) {
                return Optional.empty();
            }

            List<Pair<T, Rational>> entries = new ArrayList<>();

            if(nbt instanceof NbtList list) {
                boolean weightedList = true;

                for(NbtElement child : list) {
                    if(!(child instanceof NbtCompound object) || !object.contains("weight")) {
                        weightedList = false;
                        break;
                    }
                }

                if(weightedList) {
                    for(NbtElement child : list) {
                        NbtCompound object = (NbtCompound)child;

                        if(object.contains("value") && object.getSize() == 2) {
                            this.element.readNbt(object.get("value"), null).ifPresent(value -> {
                                entries.add(new Pair<>(value, Adapters.RATIONAL.readNbt(object.get("weight")).orElseThrow()));
                            });
                        } else {
                            this.element.readNbt(object, null).ifPresent(value -> {
                                entries.add(new Pair<>(value, Adapters.RATIONAL.readNbt(object.get("weight")).orElseThrow()));
                            });
                        }
                    }
                } else {
                    this.element.readNbt(nbt, null).ifPresent(value -> {
                        entries.add(new Pair<>(value, Rational.ONE));
                    });
                }
            } else {
                this.element.readNbt(nbt, null).ifPresent(value -> {
                    entries.add(new Pair<>(value, Rational.ONE));
                });
            }

            return Optional.of(WeightedList.build(consumer -> {
                entries.forEach(pair -> consumer.accept(pair.getLeft(), pair.getRight()));
            }));
        }

        @Override
        public Optional<JsonElement> writeJson(WeightedList<T> value) {
            if(value == null) {
                return Optional.empty();
            }

            List<Pair<JsonElement, JsonElement>> serialized = new ArrayList<>();

            for(Entry<T> entry : value.entries) {
                this.element.writeJson(entry.value, null).ifPresent(tag -> {
                    serialized.add(new Pair<>(tag, Adapters.RATIONAL.writeJson(
                            Rational.of(entry.weight, 1), null).orElseThrow()));
                });
            }

            if(serialized.size() == 1) {
                return Optional.of(serialized.getFirst().getLeft());
            }

            JsonArray array = new JsonArray();

            for(Pair<JsonElement, JsonElement> pair : serialized) {
                if(pair.getLeft() instanceof JsonObject object && !object.has("weight")) {
                    object.add("weight", pair.getRight());
                    array.add(pair.getLeft());
                } else {
                    JsonObject object = new JsonObject();
                    object.add("value", pair.getLeft());
                    object.add("weight", pair.getRight());
                    array.add(object);
                }
            }

            return Optional.of(array);
        }

        @Override
        public Optional<WeightedList<T>> readJson(JsonElement json) {
            if(json == null) {
                return Optional.empty();
            }

            List<Pair<T, Rational>> entries = new ArrayList<>();

            if(json instanceof JsonArray array) {
                boolean weightedList = true;

                for(JsonElement child : array) {
                    if(!(child instanceof JsonObject object) || !object.has("weight")) {
                        weightedList = false;
                        break;
                    }
                }

                if(weightedList) {
                    for(JsonElement child : array) {
                        JsonObject object = child.getAsJsonObject();

                        if(object.has("value") && object.size() == 2) {
                            this.element.readJson(object.get("value"), null).ifPresent(value -> {
                                entries.add(new Pair<>(value, Adapters.RATIONAL.readJson(object.get("weight")).orElseThrow()));
                            });
                        } else {
                            this.element.readJson(object, null).ifPresent(value -> {
                                entries.add(new Pair<>(value, Adapters.RATIONAL.readJson(object.get("weight")).orElseThrow()));
                            });
                        }
                    }
                } else {
                    this.element.readJson(json, null).ifPresent(value -> {
                        entries.add(new Pair<>(value, Rational.ONE));
                    });
                }
            } else {
                this.element.readJson(json, null).ifPresent(value -> {
                    entries.add(new Pair<>(value, Rational.ONE));
                });
            }

            return Optional.of(WeightedList.build(consumer -> {
                entries.forEach(pair -> consumer.accept(pair.getLeft(), pair.getRight()));
            }));
        }
    }

}
