package abeshutt.staracademy.attribute;

import java.util.function.BiFunction;
import java.util.function.Function;

public class UnaryAttributeModifier<T, A> extends NaryAttributeModifier<T> {

    protected UnaryAttributeModifier(String type, Operation<T, A> operation, Argument<A> argument) {
        super(type, (value, args) -> {
            return operation.apply(value, (Option<A>)args[0]);
        }, argument);
    }

    public static <T, A> UnaryAttributeModifier<T, A> operation(String type, Operation<T, A> operation, Argument<A> argument) {
        return new UnaryAttributeModifier<>(type, operation, argument);
    }

    public static <T, A> UnaryAttributeModifier<T, A> projection(String type, Function<A, T> mapper, Argument<A> argument) {
        return operation(type, (value, operand) -> operand.map(mapper), argument);
    }

    public static <T> UnaryAttributeModifier<T, T> projection(String type, Argument<T> argument) {
        return projection(type, Function.identity(), argument);
    }

    public static <T, A> UnaryAttributeModifier<T, A> arithmetic(String type, BiFunction<T, A, T> operation, Argument<A> argument) {
        return operation(type, (value, operand) -> {
            if(value.isAbsent() || operand.isAbsent()) {
                return Option.absent();
            }

            return Option.present(operation.apply(value.get(), operand.get()));
        }, argument);
    }

    public interface Operation<T, A> {
        Option<T> apply(Option<T> value, Option<A> argument);
    }

}