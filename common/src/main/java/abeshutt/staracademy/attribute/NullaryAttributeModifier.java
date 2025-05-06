package abeshutt.staracademy.attribute;

import java.util.function.Function;

public class NullaryAttributeModifier<T> extends NaryAttributeModifier<T> {

    protected NullaryAttributeModifier(String type, Operation<T> operation) {
        super(type, (value, args) -> {
            return operation.apply(value);
        });
    }

    public static <T> NullaryAttributeModifier<T> operation(String type, Operation<T> operation) {
        return new NullaryAttributeModifier<>(type, operation);
    }

    public static <T> NullaryAttributeModifier<T> identity(String type) {
        return operation(type, value -> value);
    }

    public static <T> NullaryAttributeModifier<T> arithmetic(String type, Function<T, T> operation) {
        return new NullaryAttributeModifier<>(type, value -> value.map(operation));
    }

    public interface Operation<T> {
        Option<T> apply(Option<T> value);
    }

}