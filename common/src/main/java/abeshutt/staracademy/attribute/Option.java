package abeshutt.staracademy.attribute;

import java.util.function.Function;

public abstract class Option<T> {

    public static <T> Option<T> present(T value) {
        return PresentOption.of(value);
    }

    public static<T> Option<T> absent() {
        return AbsentOption.instance();
    }

    public abstract T get();

    public abstract boolean isPresent();

    public abstract boolean isAbsent();

    public abstract <U> Option<U> map(Function<? super T, ? extends U> mapper);

    public abstract <U> Option<U> mapFlat(Function<? super T, ? extends Option<U>> mapper);

    public abstract T orElse(T other);

}
