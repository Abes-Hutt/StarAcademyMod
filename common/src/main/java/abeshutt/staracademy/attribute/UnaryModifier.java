package abeshutt.staracademy.attribute;

import java.util.function.BiFunction;
import java.util.function.Function;

public class UnaryModifier<T, A> extends NaryModifier<T> {

    protected UnaryModifier(String type, Operation<T, A> operation, Argument<A> argument) {
        super(type, (value, args) -> {
            return operation.apply(value, (Option<A>)args[0]);
        }, argument);
    }

    public static <T, A> UnaryModifier<T, A> operation(String type, Operation<T, A> operation, Argument<A> argument) {
        return new UnaryModifier<>(type, operation, argument);
    }

    public static <T, A> UnaryModifier<T, A> projection(String type, Function<A, T> mapper, Argument<A> argument) {
        return operation(type, (value, operand) -> operand.map(mapper), argument);
    }

    public static <T> UnaryModifier<T, T> projection(String type, Argument<T> argument) {
        return projection(type, Function.identity(), argument);
    }

    public static <T, A> UnaryModifier<T, A> arithmetic(String type, BiFunction<T, A, T> operation, Argument<A> argument) {
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