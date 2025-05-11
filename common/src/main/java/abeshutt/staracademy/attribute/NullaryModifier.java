package abeshutt.staracademy.attribute;

import java.util.function.Function;

public class NullaryModifier<T> extends NaryModifier<T> {

    protected NullaryModifier(String type, Operation<T> operation) {
        super(type, (value, args) -> {
            return operation.apply(value);
        });
    }

    public static <T> NullaryModifier<T> operation(String type, Operation<T> operation) {
        return new NullaryModifier<>(type, operation);
    }

    public static <T> NullaryModifier<T> identity(String type) {
        return operation(type, value -> value);
    }

    public static <T> NullaryModifier<T> arithmetic(String type, Function<T, T> operation) {
        return new NullaryModifier<>(type, value -> value.map(operation));
    }

    public interface Operation<T> {
        Option<T> apply(Option<T> value);
    }

}