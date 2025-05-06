package abeshutt.staracademy.attribute;

import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.math.Rational;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.*;
import java.util.function.Supplier;

public abstract class Attribute<T> implements ISerializable<NbtCompound, JsonObject> {

    protected final Map<Object, List<AttributeReference<T>>> keyedModifiers;
    protected final List<AttributeReference<T>> orderedModifiers;

    protected final List<Attribute<?>> children;

    public Attribute() {
        this.keyedModifiers = new HashMap<>();
        this.orderedModifiers = new ArrayList<>();

        this.children = new ArrayList<>();
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

    public AttributeReference<T> add(AttributeModifier<T> modifier) {
        return this.add(new AttributeReference<T>(null, 0).set(modifier));
    }

    public AttributeReference<T> add(Object owner, AttributeModifier<T> modifier) {
        return this.add(new AttributeReference<T>(owner, 0).set(modifier));
    }

    public AttributeReference<T> add(AttributeModifier<T> modifier, int order) {
        return this.add(new AttributeReference<T>(null, order).set(modifier));
    }

    public AttributeReference<T> add(Object owner, AttributeModifier<T> modifier, int order) {
        return this.add(new AttributeReference<T>(owner, order).set(modifier));
    }

    public AttributeReference<T> add(AttributeReference<T> modifier) {
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
        return modifier;
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
