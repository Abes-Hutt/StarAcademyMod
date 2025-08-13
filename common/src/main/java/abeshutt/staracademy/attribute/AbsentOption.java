package abeshutt.staracademy.attribute;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class AbsentOption<T> extends Option<T> {

    private static final AbsentOption<?> INSTANCE = new AbsentOption<>();

    protected AbsentOption() {

    }

    public static <T> AbsentOption<T> instance() {
        return (AbsentOption<T>)INSTANCE;
    }

    @Override
    public T get() {
        throw new NoSuchElementException();
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isAbsent() {
        return true;
    }

    @Override
    public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        return Option.absent();
    }

    @Override
    public <U> Option<U> mapFlat(Function<? super T, ? extends Option<U>> mapper) {
        return Option.absent();
    }

    @Override
    public T orElse(T other) {
        return other;
    }

}
