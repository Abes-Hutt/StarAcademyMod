package abeshutt.staracademy.attribute;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.IAdapter;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public class NaryModifier<T> extends Modifier<T> {
    private final String type;
    private final Operation<T> operation;
    private final Argument<?>[] arguments;

    protected NaryModifier(String type, Operation<T> operation, Argument<?>... arguments) {
        this.type = type;
        this.operation = operation;
        this.arguments = arguments;
    }

    public String getType() {
        return this.type;
    }

    public Operation<T> getOperation() {
        return this.operation;
    }

    public Argument<?>[] getArguments() {
        return this.arguments;
    }

    @Override
    public Option<T> apply(Option<T> value) {
        Option<?>[] args = new Option[this.arguments.length];

        for(int i = 0; i < args.length; i++) {
           args[i] = this.arguments[i].get();
        }

        return this.operation.apply(value, args);
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(json -> {
            for(Argument<?> argument : this.getArguments()) {
                argument.writeJson().ifPresent(tag -> json.add(argument.getName(), tag));
            }

            return json;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);

        for(Argument<?> argument : this.getArguments()) {
            argument.readJson(json.get(argument.getName()));
        }
    }

    public interface Operation<T> {
        Option<T> apply(Option<T> value, Option<?>[] args);
    }

    public static <T> Argument<T> constant(String name, T value, IAdapter<T, ? extends NbtElement, ? extends JsonElement, ?> adapter) {
        return new ConstantArgument<>(name, Option.present(value), adapter);
    }

    public static <T> Argument<T> attribute(String name, String path) {
        return new AttributeArgument<>(name, path);
    }

    public static abstract class Argument<T> implements ISerializable<NbtElement, JsonElement> {
        private final String name;

        public Argument(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public abstract Option<T> get();
    }

    protected static class ConstantArgument<T> extends Argument<T> {
        private Option<T> value;
        private final IAdapter<T, NbtElement, JsonElement, ?> adapter;

        public ConstantArgument(String name, Option<T> value, IAdapter<T, ?, ?, ?> adapter) {
            super(name);
            this.value = value;
            this.adapter = (IAdapter)adapter;
        }

        @Override
        public Option<T> get() {
            return this.value;
        }

        @Override
        public Optional<JsonElement> writeJson() {
            return this.value.isPresent() ? this.adapter.writeJson(this.value.get(), null) : Optional.empty();
        }

        @Override
        public void readJson(JsonElement json) {
            Optional<T> optional = this.adapter.readJson(json, null);
            this.value = optional.map(Option::present).orElseGet(Option::absent);
        }
    }

    protected static class AttributeArgument<T> extends Argument<T> {
        private String path;

        public AttributeArgument(String name, String path) {
            super(name);
            this.path = path;
        }

        @Override
        public Option<T> get() {
            return null; //TODO
        }

        @Override
        public Optional<JsonElement> writeJson() {
            return Optional.of(new JsonPrimitive(this.path));
        }

        @Override
        public void readJson(JsonElement json) {
            this.path = Adapters.UTF_8.readJson(json).orElseThrow();
        }
    }
}
