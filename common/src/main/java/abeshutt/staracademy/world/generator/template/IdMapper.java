package abeshutt.staracademy.world.generator.template;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class IdMapper<T> implements Iterable<T> {

    protected int nextId;
    protected final Object2IntMap<T> tToId;
    protected final List<T> idToT;

    public IdMapper() {
        this(512);
    }

    public IdMapper(int pExpectedSize) {
        this.idToT = Lists.newArrayListWithExpectedSize(pExpectedSize);
        this.tToId = new Object2IntOpenCustomHashMap<>(pExpectedSize, IdentityStrategy.INSTANCE);
        this.tToId.defaultReturnValue(-1);
    }

    public void addMapping(T pKey, int pValue) {
        this.tToId.put(pKey, pValue);

        while(this.idToT.size() <= pValue) {
            this.idToT.add(null);
        }

        this.idToT.set(pValue, pKey);

        if(this.nextId <= pValue) {
            this.nextId = pValue + 1;
        }
    }

    public void add(T key) {
        this.addMapping(key, this.nextId);
    }

    public int getId(T value) {
        return this.tToId.getInt(value);
    }

    public final T byId(int id) {
        return id >= 0 && id < this.idToT.size() ? this.idToT.get(id) : null;
    }

    public Iterator<T> iterator() {
        return Iterators.filter(this.idToT.iterator(), Objects::nonNull);
    }

    public boolean contains(int id) {
        return this.byId(id) != null;
    }

    public int size() {
        return this.tToId.size();
    }

    public T byIdOrThrow(int pId) {
        T t = this.byId(pId);
        if (t == null) {
            throw new IllegalArgumentException("No value with model " + pId);
        } else {
            return t;
        }
    }

    enum IdentityStrategy implements Hash.Strategy<Object> {
        INSTANCE;

        @Override
        public int hashCode(Object object) {
            return System.identityHashCode(object);
        }

        @Override
        public boolean equals(Object object, Object other) {
            return object == other;
        }
    }

}
