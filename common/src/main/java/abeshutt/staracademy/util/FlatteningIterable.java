package abeshutt.staracademy.util;

import java.util.Arrays;
import java.util.Iterator;

public class FlatteningIterable<T> implements Iterable<T> {

    private final Iterable<Iterable<T>> children;

    public FlatteningIterable(Iterable<Iterable<T>> children) {
        this.children = children;
    }

    @SafeVarargs
    public FlatteningIterable(Iterable<T>... children) {
        this.children = Arrays.asList(children);
    }

    @Override
    public Iterator<T> iterator() {
        return new FlatteningIterator<>(new MappingIterator<>(this.children.iterator(), Iterable::iterator));
    }

}
