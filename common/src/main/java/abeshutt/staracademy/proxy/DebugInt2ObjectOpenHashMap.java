package abeshutt.staracademy.proxy;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class DebugInt2ObjectOpenHashMap<T> extends Int2ObjectOpenHashMap<T> {

    private final Supplier<Thread> mainThread;
    private int activeIterators = 0;
    private final ThreadLocal<Integer> iteratorMutationDepth =
            ThreadLocal.withInitial(() -> 0);

    public DebugInt2ObjectOpenHashMap(Supplier<Thread> mainThread) {
        this.mainThread = mainThread;
    }

    public void checkThread() {
        Thread expected = this.mainThread.get();
        Thread current = Thread.currentThread();
        if (expected != null && current != expected) {
            throw new RuntimeException("Off-thread access! current=" + current.getName()
                    + " expected=" + expected.getName());
        }
    }

    private void checkMutate(String op) {
        checkThread();
        if (activeIterators > 0 && iteratorMutationDepth.get() == 0) {
            throw new RuntimeException(
                    "Mutation while iterating entityTrackers: " + op +
                            " size=" + super.size() +
                            " activeIterators=" + activeIterators +
                            " thread=" + Thread.currentThread().getName(),
                    new Exception("Mutation stack")
            );
        }
    }

    @Override
    public T put(Integer key, T value) {
        checkMutate("put(Integer)");
        return super.put(key, value);
    }

    @Override
    public T put(int k, T t) {
        checkMutate("put(int)");
        return super.put(k, t);
    }

    @Override
    public T remove(Object key) {
        checkMutate("remove(Object)");
        return super.remove(key);
    }

    @Override
    public T remove(int k) {
        checkMutate("remove(int)");
        return super.remove(k);
    }

    @Override
    public void clear() {
        checkMutate("clear()");
        super.clear();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends T> m) {
        checkMutate("putAll(size=" + m.size() + ")");
        super.putAll(m);
    }

    @Override
    public void ensureCapacity(int capacity) {
        checkMutate("ensureCapacity(" + capacity + ")");
        super.ensureCapacity(capacity);
    }

    @Override
    public boolean trim() {
        checkMutate("trim()");
        return super.trim();
    }

    @Override
    public boolean trim(int n) {
        checkMutate("trim(" + n + ")");
        return super.trim(n);
    }

    @Override
    protected void rehash(int newN) {
        checkMutate("rehash(" + newN + ")");
        super.rehash(newN);
    }

    @Override
    public T putIfAbsent(int k, T t) {
        checkMutate("putIfAbsent(int)");
        return super.putIfAbsent(k, t);
    }

    @Override
    public boolean remove(int k, Object v) {
        checkMutate("remove(int,Object)");
        return super.remove(k, v);
    }

    @Override
    public boolean replace(int k, T oldValue, T t) {
        checkMutate("replace(int,old,new)");
        return super.replace(k, oldValue, t);
    }

    @Override
    public T replace(int k, T t) {
        checkMutate("replace(int,new)");
        return super.replace(k, t);
    }

    @Override
    public T computeIfAbsent(int k, IntFunction<? extends T> mappingFunction) {
        checkMutate("computeIfAbsent(int,IntFunction)");
        return super.computeIfAbsent(k, mappingFunction);
    }

    @Override
    public T computeIfAbsent(int key, Int2ObjectFunction<? extends T> mappingFunction) {
        checkMutate("computeIfAbsent(int,Int2ObjectFunction)");
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public T computeIfPresent(int k, BiFunction<? super Integer, ? super T, ? extends T> remappingFunction) {
        checkMutate("computeIfPresent(int,...)");
        return super.computeIfPresent(k, remappingFunction);
    }

    @Override
    public T compute(int k, BiFunction<? super Integer, ? super T, ? extends T> remappingFunction) {
        checkMutate("compute(int,...)");
        return super.compute(k, remappingFunction);
    }

    @Override
    public T merge(int k, T t, BiFunction<? super T, ? super T, ? extends T> remappingFunction) {
        checkMutate("merge(int,...)");
        return super.merge(k, t, remappingFunction);
    }

    @Override
    public void replaceAll(BiFunction<? super Integer, ? super T, ? extends T> function) {
        checkMutate("replaceAll(...)");
        super.replaceAll(function);
    }

    @Override public T get(Object key) { checkThread(); return super.get(key); }
    @Override public T get(int k) { checkThread(); return super.get(k); }
    @Override public boolean containsKey(int k) { checkThread(); return super.containsKey(k); }
    @Override public boolean containsKey(Object key) { checkThread(); return super.containsKey(key); }
    @Override public boolean containsValue(Object v) { checkThread(); return super.containsValue(v); }
    @Override public T getOrDefault(int k, T defaultValue) { checkThread(); return super.getOrDefault(k, defaultValue); }
    @Override public T getOrDefault(Object key, T defaultValue) { checkThread(); return super.getOrDefault(key, defaultValue); }
    @Override public int size() { checkThread(); return super.size(); }
    @Override public boolean isEmpty() { checkThread(); return super.isEmpty(); }

    @Override
    public ObjectSet<Map.Entry<Integer, T>> entrySet() {
        checkThread();
        return (ObjectSet) this.int2ObjectEntrySet();
    }

    private <E> ObjectIterator<E> wrapIterator(ObjectIterator<E> it) {
        checkThread();
        activeIterators++;

        return new ObjectIterator<>() {
            private boolean closed = false;

            private void closeIfDone(boolean hasNext) {
                if (!hasNext && !closed) {
                    closed = true;
                    activeIterators--;
                }
            }

            @Override
            public boolean hasNext() {
                checkThread();
                boolean hn = it.hasNext();
                closeIfDone(hn);
                return hn;
            }

            @Override
            public E next() {
                checkThread();
                try {
                    E v = it.next();
                    closeIfDone(it.hasNext());
                    return v;
                } catch (NullPointerException npe) {
                    throw new RuntimeException(
                            "Fastutil iterator NPE (wrapped==null). Likely re-entrant modification.\n" +
                                    "identity=" + System.identityHashCode(DebugInt2ObjectOpenHashMap.this) +
                                    " size=" + DebugInt2ObjectOpenHashMap.super.size() +
                                    " activeIterators=" + activeIterators +
                                    " thread=" + Thread.currentThread().getName(),
                            npe
                    );
                }
            }

            @Override
            public void remove() {
                checkThread();
                int d = iteratorMutationDepth.get();
                iteratorMutationDepth.set(d + 1);
                try {
                    it.remove();
                } finally {
                    iteratorMutationDepth.set(d);
                }
            }
        };
    }

    private IntIterator wrapIntIterator(IntIterator it) {
        checkThread();
        activeIterators++;

        return new IntIterator() {
            private boolean closed = false;

            private void closeIfDone(boolean hasNext) {
                if (!hasNext && !closed) {
                    closed = true;
                    activeIterators--;
                }
            }

            @Override
            public boolean hasNext() {
                checkThread();
                boolean hn = it.hasNext();
                closeIfDone(hn);
                return hn;
            }

            @Override
            public int nextInt() {
                checkThread();
                int v = it.nextInt();
                closeIfDone(it.hasNext());
                return v;
            }

            @Override
            public void remove() {
                checkThread();
                int d = iteratorMutationDepth.get();
                iteratorMutationDepth.set(d + 1);
                try {
                    it.remove();
                } finally {
                    iteratorMutationDepth.set(d);
                }
            }
        };
    }

    private void withIteration(Runnable r) {
        checkThread();
        activeIterators++;
        try {
            r.run();
        } catch (NullPointerException npe) {
            throw new RuntimeException(
                    "Fastutil iteration NPE during fastForEach/forEach. " +
                            "size=" + super.size() +
                            " activeIterators=" + activeIterators +
                            " thread=" + Thread.currentThread().getName(),
                    npe
            );
        } finally {
            activeIterators--;
        }
    }

    @Override
    public ObjectCollection<T> values() {
        checkThread();
        ObjectCollection<T> base = super.values();
        return new AbstractObjectCollection<>() {
            @Override public ObjectIterator<T> iterator() { return wrapIterator(base.iterator()); }
            @Override public int size() { return base.size(); }
            @Override public boolean contains(Object o) { return base.contains(o); }
            @Override public void clear() { DebugInt2ObjectOpenHashMap.this.clear(); }
            @Override public void forEach(Consumer<? super T> action) {
                withIteration(() -> base.forEach(v -> { checkThread(); action.accept(v); }));
            }
        };
    }

    @Override
    public IntSet keySet() {
        checkThread();
        IntSet base = super.keySet();
        return new AbstractIntSet() {
            @Override public IntIterator iterator() { return wrapIntIterator(base.iterator()); }
            @Override public int size() { return base.size(); }
            @Override public boolean contains(int k) { return base.contains(k); }
            @Override public void clear() { DebugInt2ObjectOpenHashMap.this.clear(); }
        };
    }

    @Override
    public FastEntrySet<T> int2ObjectEntrySet() {
        checkThread();
        FastEntrySet<T> base = super.int2ObjectEntrySet();
        return new DebugFastEntrySet(base);
    }

    private final class DebugFastEntrySet extends AbstractObjectSet<Int2ObjectMap.Entry<T>>
            implements Int2ObjectMap.FastEntrySet<T> {

        private final Int2ObjectMap.FastEntrySet<T> base;

        private DebugFastEntrySet(Int2ObjectMap.FastEntrySet<T> base) {
            this.base = base;
        }

        @Override public ObjectIterator<Int2ObjectMap.Entry<T>> iterator() { return wrapIterator(base.iterator()); }
        @Override public ObjectIterator<Int2ObjectMap.Entry<T>> fastIterator() { return wrapIterator(base.fastIterator()); }

        @Override
        public void fastForEach(Consumer<? super Int2ObjectMap.Entry<T>> action) {
            withIteration(() -> base.fastForEach(e -> { checkThread(); action.accept(e); }));
        }

        @Override
        public void forEach(Consumer<? super Int2ObjectMap.Entry<T>> action) {
            withIteration(() -> base.forEach(e -> { checkThread(); action.accept(e); }));
        }

        @Override public int size() { return base.size(); }
        @Override public boolean contains(Object o) { return base.contains(o); }
        @Override public void clear() { DebugInt2ObjectOpenHashMap.this.clear(); }
    }

}