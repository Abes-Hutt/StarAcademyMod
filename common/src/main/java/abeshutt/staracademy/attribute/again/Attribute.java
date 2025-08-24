package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.attribute.again.type.AttributeTypes;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.*;

public abstract class Attribute<T> implements ISerializable<NbtElement, JsonElement> {

    protected AttributeType<T> type;
    protected Attribute<?> parent;
    protected final Map<String, Attribute<?>> children;

    protected Attribute(AttributeType<T> type) {
        this.type = type;
        this.children = new HashMap<>();
    }

    public AttributeType<T> getType() {
        return this.type;
    }

    public Attribute<?> getParent() {
        return this.parent;
    }

    public void setParent(Attribute<?> parent) {
        this.parent = parent;
    }

    public Map<String, Attribute<?>> getChildren() {
        return this.children;
    }

    public abstract Option<T> get(Option<T> value, AttributeContext context);

    protected void narrow(AttributeType<T> type) {

    }

    public void populate(AttributeContext context) {
        this.children.forEach((name, attribute) -> attribute.populate(context));
    }

    public <U> Attribute<U> root() {
        Attribute<?> current = this;

        while(current.getParent() != null) {
            current = current.getParent();
        }

        return (Attribute<U>)current;
    }


    public <U> Optional<Attribute<U>> path(AttributePath<U> path) {
        if(path.isAbsolute()) {
            return this.root().path(path.toRelative());
        }

        if(!path.isEmpty()) {
            return path.split((part, remainder) -> {
                if(part.equals("..")) {
                    return this.getParent().path(remainder);
                } else if(part.equals(".")) {
                    return this.path(remainder);
                } else {
                    return Optional.ofNullable(this.getChildren().get(part)).flatMap(a -> a.path(remainder));
                }
            });
        }

        return Optional.of((Attribute<U>)this);
    }

    public Attribute<?> addChild(String name, Attribute<?> attribute) {
        this.children.put(name, attribute);
        attribute.setParent(this);
        return this;
    }

    @Override
    public Optional<NbtElement> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            this.children.forEach((name, attribute) -> {
                Adapters.ATTRIBUTE.writeNbt(attribute, any()).ifPresent(tag -> {
                    nbt.put("/" + name, tag);
                });
            });

            return nbt;
        });
    }

    @Override
    public void readNbt(NbtElement nbt) {
        this.children.clear();

        if(!(nbt instanceof NbtCompound compound)) {
            return;
        }

        for(String key : compound.getKeys()) {
            if(key.startsWith("/")) {
                String name = key.substring(1);

                Adapters.ATTRIBUTE.readNbt(compound.get(key), any()).ifPresent(child -> {
                    this.children.put(name, child);
                    child.setParent(this);
                });
            }
        }
    }

    public void iterate(Consumer<Attribute<?>> action) {
        action.accept(this);
        this.children.forEach((s, attribute) -> attribute.iterate(action));
    }

    public Attribute<T> copy() {
        NbtElement nbt = Adapters.ATTRIBUTE.writeNbt(this, this.type).orElse(null);
        return (Attribute<T>)Adapters.ATTRIBUTE.readNbt(nbt, this.type).orElseThrow();
    }

    public static class Adapter implements IAdapter<Attribute<?>, NbtElement, JsonElement, AttributeType<?>> {
        @Override
        public void writeBits(Attribute<?> value, BitBuffer buffer, AttributeType<?> context) {
            Adapters.GENERIC_NBT.asNullable().writeBits(this.writeNbt(value, context).orElse(null), buffer);
        }

        @Override
        public Optional<Attribute<?>> readBits(BitBuffer buffer, AttributeType<?> context) {
            Optional<NbtElement> nbt = Adapters.GENERIC_NBT.asNullable().readBits(buffer, context);
            return Adapters.ATTRIBUTE.readNbt(nbt.orElse(null), context);
        }

        @Override
        public Optional<NbtElement> writeNbt(Attribute<?> value, AttributeType<?> context) {
            if(value instanceof ReferenceAttribute<?>) {
                return value.writeNbt();
            }

            String type = switch(value) {
                case NodeAttribute<?> ignored -> "node";
                case AssignAttribute<?> ignored -> "assign";
                case AddAttribute<?> ignored -> "add";
                case MultiplyAttribute<?> ignored -> "multiply";
                case CardScalarAttribute<?> ignored -> "card_scalar";
                default -> null;
            };

            Optional<NbtElement> element = value.writeNbt();

            return element.map(tag -> {
                if(tag instanceof NbtCompound compound && !(value instanceof ValueAttribute<?,?,?>)) {
                    if(type != null) {
                        if(!type.equals("node") || !compound.contains("modifiers")) {
                            compound.putString("type", type);
                        }
                    } else {
                        throw new UnsupportedOperationException("Unknown attribute: " + value.getClass().getSimpleName());
                    }
                }

                return tag;
            });
        }

        @Override
        public Optional<Attribute<?>> readNbt(NbtElement nbt, AttributeType<?> context) {
            context = context.simplify();
            Attribute<?> attribute = null;

            if(nbt instanceof NbtCompound compound) {
                String type = Adapters.UTF_8.readNbt(compound.get("type")).orElse(null);

                if(type == null && (compound.contains("modifiers") || compound.getKeys().stream().anyMatch(s -> s.startsWith("/")))) {
                    type = "node";
                }

                if("add".equals(type) && intersection(context, number()).simplify().equals(number())) {
                    attribute = new AddAttribute<>(number());
                } else if("multiply".equals(type) && intersection(context, number()).simplify().equals(number())) {
                    attribute = new MultiplyAttribute<>(number());
                } else if("assign".equals(type) && context.equals(number())) {
                    attribute = new AssignAttribute<>(context);
                } else if("node".equals(type)) {
                    attribute = new NodeAttribute<>(context);
                } else if("card_scalar".equals(type)) {
                    attribute = new CardScalarAttribute<>(context);
                } else if(context.equals(number())) {
                    attribute = new NumberConstantAttribute();
                }
            } else if(nbt instanceof NbtString string && (string.asString().startsWith("/")
                    || string.asString().startsWith("./") || string.asString().startsWith("../"))) {
                attribute = new ReferenceAttribute<>(context, null);
            } else if(context.simplify().equals(number())) {
                attribute = new NumberConstantAttribute();
            }

            if(attribute != null) {
                attribute.readNbt(nbt);
            }

            return Optional.ofNullable(attribute);
        }

        @Override
        public Optional<JsonElement> writeJson(Attribute<?> value, AttributeType<?> context) {
            return this.writeNbt(value, context).flatMap(tag -> {
                return Adapters.GENERIC_NBT.writeJson(tag, context);
            });
        }

        @Override
        public Optional<Attribute<?>> readJson(JsonElement json, AttributeType<?> context) {
            return Adapters.GENERIC_NBT.readJson(json, context).flatMap(tag -> {
                return Adapters.ATTRIBUTE.readNbt(tag, context);
            });
        }

        @Override
        public JsonElement serialize(Attribute<?> value, Type source, JsonSerializationContext context) {
            return this.writeJson(value, AttributeTypes.any()).orElse(JsonNull.INSTANCE);
        }

        @Override
        public Attribute<?> deserialize(JsonElement json, Type source, JsonDeserializationContext context) {
            return this.readJson(json, AttributeTypes.any()).orElse(null);
        }
    }

}
