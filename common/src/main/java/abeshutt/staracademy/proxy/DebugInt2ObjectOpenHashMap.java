package abeshutt.staracademy.proxy;

import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DebugInt2ObjectOpenHashMap<T> extends Int2ObjectOpenHashMap<T> {

    private final Supplier<Thread> mainThread;

    public DebugInt2ObjectOpenHashMap(Supplier<Thread> mainThread) {
        this.mainThread = mainThread;
    }

    public void check() {
        if (Thread.currentThread() != this.mainThread.get()) {
            throw new RuntimeException("IT'S BROKEN!!!!!!!!!!!!!!!!");
        }
    }

    @Override
    public ObjectSet<Map.Entry<Integer, T>> entrySet() {
        this.check();
        return super.entrySet();
    }

    @Override
    public T put(Integer key, T value) {
        this.check();
        return super.put(key, value);
    }

    @Override
    public T get(Object key) {
        this.check();
        return super.get(key);
    }

    @Override
    public T remove(Object key) {
        this.check();
        return super.remove(key);
    }

    @Override
    public boolean containsKey(Object key) {
        this.check();
        return super.containsKey(key);
    }

    @Override
    public void forEach(BiConsumer<? super Integer, ? super T> consumer) {
        this.check();
        super.forEach(consumer);
    }

    @Override
    public T getOrDefault(Object key, T defaultValue) {
        this.check();
        return super.getOrDefault(key, defaultValue);
    }

    @Override
    public T computeIfAbsentPartial(int key, Int2ObjectFunction<? extends T> mappingFunction) {
        this.check();
        return super.computeIfAbsentPartial(key, mappingFunction);
    }

    @Override
    public T apply(int operand) {
        this.check();
        return super.apply(operand);
    }

    @Override
    public <T1> Function<T1, T> compose(Function<? super T1, ? extends Integer> before) {
        this.check();
        return super.compose(before);
    }

    @Override
    public Int2ByteFunction andThenByte(Object2ByteFunction<T> after) {
        this.check();
        return super.andThenByte(after);
    }

    @Override
    public Byte2ObjectFunction<T> composeByte(Byte2IntFunction before) {
        this.check();
        return super.composeByte(before);
    }

    @Override
    public Int2ShortFunction andThenShort(Object2ShortFunction<T> after) {
        this.check();
        return super.andThenShort(after);
    }

    @Override
    public Short2ObjectFunction<T> composeShort(Short2IntFunction before) {
        this.check();
        return super.composeShort(before);
    }

    @Override
    public Int2IntFunction andThenInt(Object2IntFunction<T> after) {
        this.check();
        return super.andThenInt(after);
    }

    @Override
    public Int2ObjectFunction<T> composeInt(Int2IntFunction before) {
        this.check();
        return super.composeInt(before);
    }

    @Override
    public Int2LongFunction andThenLong(Object2LongFunction<T> after) {
        this.check();
        return super.andThenLong(after);
    }

    @Override
    public Long2ObjectFunction<T> composeLong(Long2IntFunction before) {
        this.check();
        return super.composeLong(before);
    }

    @Override
    public Int2CharFunction andThenChar(Object2CharFunction<T> after) {
        this.check();
        return super.andThenChar(after);
    }

    @Override
    public Char2ObjectFunction<T> composeChar(Char2IntFunction before) {
        this.check();
        return super.composeChar(before);
    }

    @Override
    public Int2FloatFunction andThenFloat(Object2FloatFunction<T> after) {
        this.check();
        return super.andThenFloat(after);
    }

    @Override
    public Float2ObjectFunction<T> composeFloat(Float2IntFunction before) {
        this.check();
        return super.composeFloat(before);
    }

    @Override
    public Int2DoubleFunction andThenDouble(Object2DoubleFunction<T> after) {
        this.check();
        return super.andThenDouble(after);
    }

    @Override
    public Double2ObjectFunction<T> composeDouble(Double2IntFunction before) {
        this.check();
        return super.composeDouble(before);
    }

    @Override
    public <T1> Int2ObjectFunction<T1> andThenObject(Object2ObjectFunction<? super T, ? extends T1> after) {
        this.check();
        return super.andThenObject(after);
    }

    @Override
    public <T1> Object2ObjectFunction<T1, T> composeObject(Object2IntFunction<? super T1> before) {
        this.check();
        return super.composeObject(before);
    }

    @Override
    public <T1> Int2ReferenceFunction<T1> andThenReference(Object2ReferenceFunction<? super T, ? extends T1> after) {
        this.check();
        return super.andThenReference(after);
    }

    @Override
    public <T1> Reference2ObjectFunction<T1, T> composeReference(Reference2IntFunction<? super T1> before) {
        this.check();
        return super.composeReference(before);
    }

    @Override
    public T apply(Integer key) {
        this.check();
        return super.apply(key);
    }

    @Override
    public void replaceAll(BiFunction<? super Integer, ? super T, ? extends T> function) {
        this.check();
        super.replaceAll(function);
    }

    @Override
    public @Nullable T putIfAbsent(Integer key, T value) {
        this.check();
        return super.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.check();
        return super.remove(key, value);
    }

    @Override
    public boolean replace(Integer key, T oldValue, T newValue) {
        this.check();
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public @Nullable T replace(Integer key, T value) {
        this.check();
        return super.replace(key, value);
    }

    @Override
    public T computeIfAbsent(Integer key, @NotNull Function<? super Integer, ? extends T> mappingFunction) {
        this.check();
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public T computeIfPresent(Integer key, @NotNull BiFunction<? super Integer, ? super T, ? extends T> remappingFunction) {
        this.check();
        return super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public T compute(Integer key, @NotNull BiFunction<? super Integer, ? super @Nullable T, ? extends T> remappingFunction) {
        this.check();
        return super.compute(key, remappingFunction);
    }

    @Override
    public T merge(Integer key, @NotNull T value, @NotNull BiFunction<? super T, ? super T, ? extends T> remappingFunction) {
        this.check();
        return super.merge(key, value, remappingFunction);
    }

    @Override
    public @NotNull <V> Function<Integer, V> andThen(@NotNull Function<? super T, ? extends V> after) {
        this.check();
        return super.andThen(after);
    }

}