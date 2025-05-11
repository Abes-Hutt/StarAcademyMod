package abeshutt.staracademy.attribute;

import org.apache.commons.lang3.function.TriFunction;

public class BinaryModifier<T, A, B> extends NaryModifier<T> {

    protected BinaryModifier(String type, Operation<T, A, B> operation, Argument<A> argument1, Argument<B> argument2) {
        super(type, (value, args) -> {
            return operation.apply(value, (Option<A>)args[0], (Option<B>)args[1]);
        }, argument1, argument2);
    }

    public static <T, A, B> BinaryModifier<T, A, B> operation(String type, Operation<T, A, B> operation, Argument<A> argument1, Argument<B> argument2) {
        return new BinaryModifier<>(type, operation, argument1, argument2);
    }

    public static <T, A, B> BinaryModifier<T, A, B> arithmetic(String type, TriFunction<T, A, B, T> operation, Argument<A> argument1, Argument<B> argument2) {
        return operation(type, (value, operand1, operand2) -> {
            if(value.isAbsent() || operand1.isAbsent() || operand2.isAbsent()) {
                return Option.absent();
            }

            return Option.present(operation.apply(value.get(), operand1.get(), operand2.get()));
        }, argument1, argument2);
    }

    public interface Operation<T, A, B> {
        Option<T> apply(Option<T> value, Option<A> argument1, Option<B> argument2);
    }

}