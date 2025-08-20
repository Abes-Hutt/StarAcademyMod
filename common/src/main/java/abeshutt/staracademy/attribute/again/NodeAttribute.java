package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.*;
import java.util.function.Consumer;

public class NodeAttribute<T> extends Attribute<T> {

    protected Map<Object, List<Modifier<T>>> keyedListeners;
    protected List<Modifier<T>> orderedModifiers;

    protected NodeAttribute(AttributeType<T> type) {
        super(type);
        this.keyedListeners = new HashMap<>();
        this.orderedModifiers = new ArrayList<>();
    }

    public static <T> NodeAttribute<T> of(AttributeType<T> type) {
        return new NodeAttribute<>(type);
    }

    public List<Modifier<T>> getModifiers() {
        return this.orderedModifiers;
    }

    public NodeAttribute<T> add(Modifier<T> modifier) {
        List<Modifier<T>> keyed = this.keyedListeners.computeIfAbsent(modifier.getOwner(),
                key -> new ArrayList<>());
        keyed.add(modifier);

        List<Modifier<T>> ordered = this.orderedModifiers;
        int index = Collections.binarySearch(ordered, modifier);

        if(index >= 0) {
            while(index < ordered.size() - 1 && ordered.get(index + 1).compareTo(modifier) == 0) {
                index++;
            }

            index++;
        } else {
            index = -index - 1;
        }

        ordered.add(index, modifier);
        modifier.getAttribute().setParent(this);
        return this;
    }

    public NodeAttribute<T> add(Object owner, int order, Attribute<T> attribute) {
        return this.add(new Modifier<>(owner, order, attribute));
    }

    public NodeAttribute<T> remove(Object owner) {
        List<Modifier<T>> listeners = this.keyedListeners.remove(owner);
        if(listeners == null || listeners.isEmpty()) return this;
        this.orderedModifiers.removeAll(new HashSet<>(listeners));

        for(Modifier<T> listener : listeners) {
           listener.getAttribute().setParent(null);
        }

        return this;
    }

    @Override
    public Option<T> get(Option<T> value, AttributeContext context) {
        for(Modifier<T> modifier : this.orderedModifiers) {
            value = modifier.getAttribute().get(value, context);
        }

        return value;
    }

    @Override
    public void populate(AttributeContext context) {
        for(Modifier<T> modifier : this.orderedModifiers) {
            modifier.getAttribute().populate(context);
        }

        super.populate(context);
    }

    public void iterate(Consumer<Attribute<?>> action) {
        super.iterate(action);

        for(Modifier<T> modifier : this.orderedModifiers) {
            modifier.getAttribute().iterate(action);
        }
    }

    public static class Modifier<T> implements Comparable<Modifier<T>> {
        private Object owner;
        private int order;
        private Attribute<?> attribute;

        protected Modifier() {
            this(null, 0, null);
        }

        public Modifier(Object owner, int order, Attribute<T> attribute) {
            this.owner = owner;
            this.order = order;
            this.attribute = attribute;
        }

        public Object getOwner() {
            return this.owner;
        }

        public int getOrder() {
            return this.order;
        }

        public Attribute<T> getAttribute() {
            return (Attribute<T>)this.attribute;
        }

        public Optional<NbtCompound> writeNbt(AttributeType<?> context) {
            return Adapters.ATTRIBUTE.writeNbt(this.attribute, context).map(tag -> {
                if(tag instanceof NbtCompound compound) {
                    if(this.order != 0) {
                        Adapters.INT.writeNbt(this.order).ifPresent(e -> compound.put("order", e));
                    }

                    return compound;
                }

                NbtCompound wrapper = new NbtCompound();
                wrapper.put("attribute", tag);
                Adapters.INT.writeNbt(this.order).ifPresent(e -> wrapper.put("order", e));
                return wrapper;
            });
        }

        public void readNbt(NbtCompound nbt, AttributeType<?> context) {
            this.order = Adapters.INT.readNbt(nbt.get("order")).orElse(0);

            NbtElement attribute = nbt;

            if(nbt.contains("attribute")) {
                attribute = nbt.get("attribute");
            }

            this.attribute = Adapters.ATTRIBUTE.readNbt(attribute, context).orElseThrow();
        }

        @Override
        public int compareTo(Modifier<T> other) {
            return Integer.compare(this.order, other.getOrder());
        }
    }

    @Override
    public Optional<NbtElement> writeNbt() {
        return super.writeNbt().map(nbt -> {
            if(nbt instanceof NbtCompound compound) {
                NbtList modifiers = new NbtList();

                for(Modifier<T> modifier : this.orderedModifiers) {
                    modifier.writeNbt(this.type).ifPresent(modifiers::add);
                }

                compound.put("modifiers", modifiers);
            }

            return nbt;
        });
    }

    @Override
    public void readNbt(NbtElement nbt) {
        super.readNbt(nbt);
        this.orderedModifiers.clear();

        if(nbt instanceof NbtCompound compound) {
            if(compound.get("modifiers") instanceof NbtList list) {
                for(int i = 0; i < list.size(); i++) {
                    NbtCompound element = list.getCompound(i);
                    Modifier<T> modifier = new Modifier<>();
                    modifier.readNbt(element, this.type);
                    this.add(modifier);
                }
            }
        }
    }

}
