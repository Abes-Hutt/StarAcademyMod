package abeshutt.staracademy.attribute.modifier;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.type.AttributeType;

import java.util.function.BiFunction;
import java.util.function.Function;

public class UnaryAttribute<T, A> extends NaryAttribute<T> {

    protected UnaryAttribute(AttributeType<T> type, Operation<T, A> operation, ArgumentDefinition<A> argument) {
        super(type, (value, args) -> {
            return operation.apply(value, (Option<A>)args[0]);
        }, argument);
    }

    public static class Projection<T, A> extends UnaryAttribute<T, A> {
        protected Projection(AttributeType<T> type, Function<A, T> mapper, ArgumentDefinition<A> argument) {
            super(type, (value, operand) -> operand.map(mapper), argument);
        }
    }

    public static class Arithmetic<T, A> extends UnaryAttribute<T, A> {
        protected Arithmetic(AttributeType<T> type, BiFunction<T, A, T> operation, ArgumentDefinition<A> argument) {
            super(type, (value, operand) -> {
                if(value.isAbsent() || operand.isAbsent()) {
                    return Option.absent();
                }

                return Option.present(operation.apply(value.get(), operand.get()));
            }, argument);
        }
    }

    public interface Operation<T, A> {
        Option<T> apply(Option<T> value, Option<A> argument);
    }

}