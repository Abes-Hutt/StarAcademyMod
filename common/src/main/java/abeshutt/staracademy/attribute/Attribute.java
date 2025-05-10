package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.path.AttributePath;
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

    protected final Map<Object, List<AttributeModifierInstance<T>>> keyedModifiers;
    protected final List<AttributeModifierInstance<T>> orderedModifiers;

    protected AttributeParent parent;
    protected final Map<String, Attribute<?>> children;

    public Attribute() {
        this.keyedModifiers = new HashMap<>();
        this.orderedModifiers = new ArrayList<>();

        this.children = new HashMap<>();
    }

    public Option<T> get(AttributeContext context) {
        Option<T> value = Option.absent();

        for(AttributeModifierInstance<T> modifier : this.orderedModifiers) {
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

    public <U> Attribute<U> root() {
        Attribute<?> current = this;

        while(current.getParent() != null) {
            current = current.getParent().get();
        }

        return (Attribute<U>)current;
    }

    public <U> Attribute<U> path(AttributePath<U> path) {
        if(path.isAbsolute()) {
            return this.root().path(path.toRelative());
        }

        if(!path.isEmpty()) {
            return path.split((part, remainder) -> {
                if(part.equals("..")) {
                    return this.getParent().get().path(remainder);
                } else if(part.equals(".")) {
                    return this.path(remainder);
                } else {
                    return this.children.get(part).path(remainder);
                }
            });
        }

        return (Attribute<U>)this;
    }

    public AttributeModifierInstance<T> add(AttributeModifier<T> modifier) {
        return this.add(null, AttributeModifierInstance.of(modifier));
    }

    public AttributeModifierInstance<T> add(Object owner, AttributeModifier<T> modifier) {
        return this.add(owner, AttributeModifierInstance.of(modifier));
    }

    public AttributeModifierInstance<T> add(AttributeModifier<T> modifier, int order) {
        return this.add(null, AttributeModifierInstance.of(order, modifier));
    }

    public AttributeModifierInstance<T> add(Object owner, AttributeModifier<T> modifier, int order) {
        return this.add(owner, AttributeModifierInstance.of(order, modifier));
    }

    public <U> AttributeModifierInstance<U> add(AttributeModifierInstance<U> modifier) {
        return this.add(null, modifier);
    }

    public <U> AttributeModifierInstance<U> add(Object owner, AttributeModifierInstance<U> modifier) {
        this.path(modifier.getPath()).addInternal(owner, modifier);
        return modifier;
    }

    protected int compare(AttributeModifierInstance <T> a, AttributeModifierInstance<T> b) {
        return Integer.compare(a.getOrder(), b.getOrder());
    }

    protected void addInternal(Object owner, AttributeModifierInstance modifier) {
        List<AttributeModifierInstance<T>> keyed = this.keyedModifiers.computeIfAbsent(owner,
                key -> new ArrayList<>());
        keyed.add(modifier);

        List<AttributeModifierInstance<T>> ordered = this.orderedModifiers;
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
        List<AttributeModifierInstance<T>> listeners = this.keyedModifiers.remove(owner);
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

    protected abstract TypeSupplierAdapter<AttributeModifier<T>> getModifierAdapter();

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            JsonArray modifiers = new JsonArray();
            AttributeModifierInstance.Adapter<T> adapter = AttributeModifierInstance.adapter(this.getModifierAdapter());

            this.orderedModifiers.forEach(modifier -> {
                adapter.writeJson(modifier).ifPresent(modifiers::add);
            });

            json.add("modifiers", modifiers);

            this.children.forEach((name, attribute) -> {

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

        public void register(Supplier<AttributeModifier<T>> modifier) {
            AttributeModifier<T> value = modifier.get();

            if(value instanceof NaryAttributeModifier<T> nary) {
                this.register(nary.getType(), null, modifier);
            }

            throw new UnsupportedOperationException("Modifier must be n-ary");
        }
    }

}
