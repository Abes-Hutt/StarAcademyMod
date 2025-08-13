package abeshutt.staracademy.attribute.modifier;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.attribute.type.AttributeType;
import abeshutt.staracademy.data.adapter.IAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public class NaryAttribute<T> extends Attribute<T> {
    private final Operation<T> operation;
    private final ArgumentDefinition<?>[] definitions;
    private final Argument<?>[] arguments;

    protected NaryAttribute(AttributeType<T> type, Operation<T> operation, ArgumentDefinition<?>... definitions) {
        super(type);
        this.type = type;
        this.operation = operation;
        this.definitions = definitions;
        this.arguments = new Argument<?>[this.definitions.length];
    }

    public Operation<T> getOperation() {
        return this.operation;
    }

    public void set(int index, Argument<?> argument) {
        this.arguments[index] = argument;
    }

    @Override
    public Option<T> get(Option<T> value) {
        Option<?>[] args = new Option[this.arguments.length];

        for(int i = 0; i < args.length; i++) {
            args[i] = this.arguments[i].get(this);
        }

        return this.operation.apply(value, args);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            for(int i = 0; i < this.definitions.length; i++) {
                ArgumentDefinition definition = this.definitions[i];
                Argument<?> argument = this.arguments[i];
                argument.writeJson(json, definition);
            }

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);

        for(int i = 0; i < this.definitions.length; i++) {
            ArgumentDefinition definition = this.definitions[i];
            JsonElement element = json.get(definition.getName());
            Argument<?> argument = new ConstantArgument<>(null);

            if(element instanceof JsonPrimitive primitive && primitive.isString()) {
                String string = primitive.getAsString();

                if(string.startsWith("/") || string.startsWith("./") || string.startsWith("../")) {
                    argument = new AttributeArgument<>(null);
                }
            }

            argument.readJson(json, definition);
            this.arguments[i] = argument;
        }
    }

    public interface Operation<T> {
        Option<T> apply(Option<T> value, Option<?>[] args);
    }

    public static <T> ArgumentDefinition<T> define(String name, IAdapter<T, ? extends NbtElement, ? extends JsonElement, ?> adapter) {
        return new ArgumentDefinition<>(name, adapter);
    }

    public static <T> Argument<T> constant(T value) {
        return new ConstantArgument<>(Option.present(value));
    }

    public static <T> Argument<T> attribute(AttributePath<T> path) {
        return new AttributeArgument<>(path);
    }

    public static class ArgumentDefinition<T> {
        private final String name;
        private final IAdapter<T, NbtElement, JsonElement, ?> adapter;

        public ArgumentDefinition(String name, IAdapter<T, ?, ?, ?> adapter) {
            this.name = name;
            this.adapter = (IAdapter)adapter;
        }

        public String getName() {
            return this.name;
        }

        public IAdapter<T, NbtElement, JsonElement, ?> getAdapter() {
            return this.adapter;
        }
    }

    public static abstract class Argument<T> {
        public abstract Option<T> get(Attribute<?> attribute);

        public abstract void writeJson(JsonObject json, ArgumentDefinition<T> definition);

        public abstract void readJson(JsonObject json, ArgumentDefinition<T> definition);
    }

    protected static class ConstantArgument<T> extends Argument<T> {
        private Option<T> value;

        public ConstantArgument(Option<T> value) {
            this.value = value;
        }

        @Override
        public Option<T> get(Attribute<?> attribute) {
            return this.value;
        }

        @Override
        public void writeJson(JsonObject json, ArgumentDefinition<T> definition) {
            if(this.value.isPresent()) {
                json.add(definition.getName(), definition.getAdapter().writeJson(this.value.get(),
                        null).orElse(JsonNull.INSTANCE));
            }
        }

        @Override
        public void readJson(JsonObject json, ArgumentDefinition<T> definition) {
            if(!json.has(definition.getName())) {
                this.value = Option.absent();
                return;
            }

            this.value = Option.present(definition.getAdapter().readJson(json.get(definition.getName()),
                    null).orElse(null));
        }
    }

    protected static class AttributeArgument<T> extends Argument<T> {
        private AttributePath<T> path;

        public AttributeArgument(AttributePath<T> path) {
            this.path = path;
        }

        @Override
        public Option<T> get(Attribute<?> attribute) {
            return attribute.path(this.path).get();
        }

        @Override
        public void writeJson(JsonObject json, ArgumentDefinition<T> definition) {
            this.path.writeJson().ifPresent(tag -> json.add(definition.getName(), tag));
        }

        @Override
        public void readJson(JsonObject json, ArgumentDefinition<T> definition) {
            this.path = AttributePath.empty();
            this.path.readJson(json.get(definition.getName()));
        }
    }
}
