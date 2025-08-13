package abeshutt.staracademy.attribute;

import java.util.function.Function;

public class PresentOption<T> extends Option<T> {

    private final T value;

    protected PresentOption(T value) {
        this.value = value;
    }

    public static <T> PresentOption<T> of(T value) {
        return new PresentOption<>(value);
    }

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isAbsent() {
        return false;
    }

    @Override
    public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        return Option.present(mapper.apply(this.value));
    }

    @Override
    public <U> Option<U> mapFlat(Function<? super T, ? extends Option<U>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public T orElse(T other) {
        return this.value;
    }

}
