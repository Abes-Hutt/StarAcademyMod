package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.parent.AttributeParent;
import abeshutt.staracademy.attribute.parent.ModifierAttributeParent;
import abeshutt.staracademy.attribute.parent.StructuralAttributeParent;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.attribute.type.AttributeType;
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

public class Attribute<T> implements ISerializable<NbtCompound, JsonObject> {
    
    protected AttributeType<T> type;
    protected AttributeParent parent;
    protected final Map<String, Attribute<?>> children;
    protected final Map<Object, List<ModifierInstance<T>>> keyedModifiers;
    protected final List<ModifierInstance<T>> orderedModifiers;

    protected Option<T> cache;

    protected Attribute(AttributeType<T> type) {
        this.type = type;
        this.parent = null;
        this.children = new HashMap<>();
        this.keyedModifiers = new HashMap<>();
        this.orderedModifiers = new ArrayList<>();
    }

    public void invalidate() {
        this.cache = null;

        if(this.parent != null) {
            this.parent.get().invalidate();
        }
    }

    public AttributeType<T> getType() {
        return this.type;
    }

    public List<Attribute<T>> getModifiers() {
        return this.orderedModifiers.stream().map(ModifierInstance::get).toList();
    }

    public Option<T> get() {
        return this.get(Option.absent());
    }

    public Option<T> get(T value) {
        return this.get(Option.present(value));
    }

    public Option<T> get(Option<T> value) {
        boolean shortcut = value.isAbsent();

        if(shortcut && this.cache != null) {
            return this.cache;
        }

        Iterator<ModifierInstance<T>> iterator = this.orderedModifiers.iterator();

        while(iterator.hasNext()) {
            ModifierInstance<T> modifier = iterator.next();

            if(modifier.isRemoved()) {
                modifier.dispose();
                this.keyedModifiers.get(modifier.getOwner()).remove(modifier);
                iterator.remove();
            } else {
                value = modifier.get().get(value);
            }
        }

        if(shortcut && this.cache == null) {
            this.cache = value;
        }

        return value;
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

    public ModifierReference<T> add(Attribute<T> modifier) {
        return this.add(null, ModifierReference.of(modifier));
    }

    public ModifierReference<T> add(Object owner, Attribute<T> modifier) {
        return this.add(owner, ModifierReference.of(modifier));
    }

    public ModifierReference<T> add(Attribute<T> modifier, int order) {
        return this.add(null, ModifierReference.of(order, modifier));
    }

    public ModifierReference<T> add(Object owner, Attribute<T> modifier, int order) {
        return this.add(owner, ModifierReference.of(order, modifier));
    }

    public <U> ModifierReference<U> add(ModifierReference<U> modifier) {
        return this.add(null, modifier);
    }

    public <U> ModifierReference<U> add(Object owner, ModifierReference<U> modifier) {
        this.path(modifier.getPath()).addInternal(owner, modifier);
        return modifier;
    }

    protected int compare(ModifierInstance<T> a, ModifierInstance<T> b) {
        return Integer.compare(a.getReference().getOrder(), b.getReference().getOrder());
    }

    protected void addInternal(Object owner, ModifierReference modifier) {
        ModifierInstance<T> instance = new ModifierInstance<T>(this, owner, modifier);

        List<ModifierInstance<T>> keyed = this.keyedModifiers.computeIfAbsent(owner,
                key -> new ArrayList<>());
        keyed.add(instance);

        List<ModifierInstance<T>> ordered = this.orderedModifiers;
        int index = Collections.binarySearch(ordered, instance, this::compare);

        if(index >= 0) {
            while(index < ordered.size() - 1 && this.compare(ordered.get(index + 1), instance) == 0) {
                index++;
            }

            index++;
        } else {
            index = -index - 1;
        }

        ordered.add(index, instance);

        for(int i = index; i < ordered.size(); i++) {
            ordered.get(i).get().setParent(new ModifierAttributeParent(this, i));
        }
    }

    public void remove(Object owner) {
        List<ModifierInstance<T>> listeners = this.keyedModifiers.remove(owner);
        if(listeners == null || listeners.isEmpty()) return;
        this.orderedModifiers.removeAll(new HashSet<>(listeners));
        listeners.forEach(ModifierInstance::dispose);
        this.invalidate();
    }

    public void clear() {
        if(this.orderedModifiers.isEmpty()) return;
        this.keyedModifiers.clear();
        this.orderedModifiers.forEach(ModifierInstance::dispose);
        this.orderedModifiers.clear();
        this.invalidate();
    }

    public <A extends Attribute<T>> A copy() {
        return (A)this;
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

    public void putChild(String name, Attribute<?> child) {
        child.setParent(new StructuralAttributeParent(this, name));
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

    @Override
    public Optional<JsonObject> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            JsonArray modifiers = new JsonArray();
            ModifierReference.Adapter<T> adapter = ModifierReference.adapter(this.type.getModifiers());

            this.orderedModifiers.forEach(modifier -> {
                //adapter.writeJson(modifier).ifPresent(orderedModifiers::add);
            });

            json.add("modifiers", modifiers);

            this.children.forEach((name, attribute) -> {

            });
            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {

    }

    protected static class ModifierAdapter<T> extends TypeSupplierAdapter<Modifier<T>> {
        public ModifierAdapter() {
            super("type", false);
        }

        @Override
        public String getType(Modifier<T> value) {
            if(value instanceof NaryModifier<?> nary) {
                return nary.getType();
            }

            return super.getType(value);
        }

        public void register(Supplier<Modifier<T>> modifier) {
            Modifier<T> value = modifier.get();

            if(value instanceof NaryModifier<T> nary) {
                this.register(nary.getType(), null, modifier);
            }

            throw new UnsupportedOperationException("Modifier must be n-ary");
        }
    }

}
