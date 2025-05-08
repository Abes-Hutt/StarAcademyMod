package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.path.*;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.item.data.RecursiveAttributeIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class Attribute<T> implements ISerializable<NbtCompound, JsonObject> {

    protected final Map<Object, List<AttributeReference<T>>> keyedModifiers;
    protected final List<AttributeReference<T>> orderedModifiers;

    protected AttributeParent parent;
    protected final Map<String, Attribute<?>> children;

    public Attribute() {
        this.keyedModifiers = new HashMap<>();
        this.orderedModifiers = new ArrayList<>();

        this.children = new HashMap<>();
    }

    public Option<T> get(AttributeContext context) {
        Option<T> value = Option.absent();

        for(AttributeReference<T> modifier : this.orderedModifiers) {
            value = modifier.get().apply(value, context);
        }

        return value;
    }

    public T getOr(T other, AttributeContext context) {
        Option<T> value = this.get(context);

        if(value.isAbsent()) {
            return other;
        }

        return value.get();
    }

    public T getOr(Supplier<T> other, AttributeContext context) {
        Option<T> value = this.get(context);

        if(value.isAbsent()) {
            return other.get();
        }

        return value.get();
    }

    protected int compare(AttributeReference<T> a, AttributeReference<T> b) {
        return Integer.compare(a.getOrder(), b.getOrder());
    }

    public boolean has(Object owner) {
        return this.keyedModifiers.containsKey(owner);
    }

    public AttributeReference<T> add(AttributeModifier<T> modifier, AttributeContext context) {
        return this.add(new AttributeReference<T>(null, 0, AttributePath.EMPTY).set(modifier), context);
    }

    public AttributeReference<T> add(Object owner, AttributeModifier<T> modifier, AttributeContext context) {
        return this.add(new AttributeReference<T>(owner, 0, AttributePath.EMPTY).set(modifier), context);
    }

    public AttributeReference<T> add(AttributeModifier<T> modifier, int order, AttributeContext context) {
        return this.add(new AttributeReference<T>(null, order, AttributePath.EMPTY).set(modifier), context);
    }

    public AttributeReference<T> add(Object owner, AttributeModifier<T> modifier, int order, AttributeContext context) {
        return this.add(new AttributeReference<T>(owner, order, AttributePath.EMPTY).set(modifier), context);
    }

    public <U> AttributeReference<U> add(AttributeReference<?> modifier, AttributeContext context) {
        this.addInternal(modifier, modifier.getPath(), context);
        return (AttributeReference<U>)modifier;
    }

    protected void addInternal(AttributeReference modifier, AttributePath path, AttributeContext context) {
        if(path.isAbsolute()) {
            context.getRoot().addInternal(modifier, path.toRelative(), context);
            return;
        }

        if(!path.isEmpty()) {
            path.split((part, remainder) -> {
                if(part.equals("..")) {
                    this.getParent().get().addInternal(modifier, remainder, context);
                } else if(part.equals(".")) {
                    this.addInternal(modifier, remainder, context);
                } else {
                    this.children.get(part).addInternal(modifier, remainder, context);
                }
            });

            return;
        }

        List<AttributeReference<T>> keyed = this.keyedModifiers.computeIfAbsent(modifier.getOwner(),
                key -> new ArrayList<>());
        keyed.add(modifier);

        List<AttributeReference<T>> ordered = this.orderedModifiers;
        int index = Collections.binarySearch(ordered, modifier, this::compare);

        if(index >= 0) {
            while(index < ordered.size() - 1 && this.compare(ordered.get(index + 1), modifier) == 0) {
                index++;
            }

            index++;
        } else {
            index = -index - 1;
        }

        ordered.add(index, modifier);
    }

    public void remove(Object owner) {
        List<AttributeReference<T>> listeners = this.keyedModifiers.remove(owner);
        if(listeners == null || listeners.isEmpty()) return;
        this.orderedModifiers.removeAll(new HashSet<>(listeners));
    }

    public void clear() {
        this.keyedModifiers.clear();
        this.orderedModifiers.clear();
    }

    public AttributeParent getParent() {
        return this.parent;
    }

    public void setParent(AttributeParent parent) {
        this.parent = parent;
    }

    public Collection<Attribute<?>> getChildren() {
        return this.children.values();
    }

    public void addChild(String name, Attribute<?> child) {
        child.setParent(new AttributeParent(this, this.children.size()));
        this.children.put(name, child);
    }

    public Iterable<Attribute<?>> getSelfAndChildren() {
        return Iterables.concat(List.of(this), this.getChildren());
    }

    public <T> Iterable<T> getChildren(Class<T> type) {
        return Iterables.filter(this.getChildren(), type);
    }

    public <T> Iterable<T> getSelfAndChildren(Class<T> type) {
        return Iterables.filter(this.getSelfAndChildren(), type);
    }

    public Stream<Attribute<?>> streamChildren() {
        return Streams.stream(this.getChildren());
    }

    public Stream<Attribute<?>> streamSelfAndChildren() {
        return Streams.stream(this.getSelfAndChildren());
    }

    public <T> Stream<T> streamChildren(Class<T> type) {
        return Streams.stream(this.getChildren(type));
    }

    public <T> Stream<T> streamSelfAndChildren(Class<T> type) {
        return Streams.stream(this.getSelfAndChildren(type));
    }

    public Iterable<Attribute<?>> getDescendants() {
        return () -> new RecursiveAttributeIterator(this);
    }

    public Iterable<Attribute<?>> getSelfAndDescendants() {
        return Iterables.concat(Collections.singleton(this), this.getDescendants());
    }

    public <T> Iterable<T> getDescendants(Class<T> type) {
        return Iterables.filter(this.getDescendants(), type);
    }

    public <T> Iterable<T> getSelfAndDescendants(Class<T> type) {
        return Iterables.filter(this.getSelfAndDescendants(), type);
    }

    public Stream<Attribute<?>> streamDescendants() {
        return Streams.stream(this.getDescendants());
    }

    public Stream<Attribute<?>> streamSelfAndDescendants() {
        return Streams.stream(this.getSelfAndDescendants());
    }

    public <T> Stream<T> streamDescendants(Class<T> type) {
        return Streams.stream(this.getDescendants(type));
    }

    public <T> Stream<T> streamSelfAndDescendants(Class<T> type) {
        return Streams.stream(this.getSelfAndDescendants(type));
    }

    protected abstract TypeSupplierAdapter<AttributeModifier<T>> getAdapter();

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            JsonArray modifiers = new JsonArray();

            this.orderedModifiers.forEach(modifier -> {

            });

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        ISerializable.super.readJson(json);
    }

    protected static class ModifierAdapter<T> extends TypeSupplierAdapter<AttributeModifier<T>> {
        public ModifierAdapter() {
            super("type", false);
        }

        @Override
        public String getType(AttributeModifier<T> value) {
            if(value instanceof NaryAttributeModifier<?> nary) {
                return nary.getType();
            }

            return super.getType(value);
        }
    }

}
