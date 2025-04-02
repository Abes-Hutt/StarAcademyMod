package abeshutt.staracademy.util;

import java.util.Arrays;
import java.util.Iterator;

public class FlatteningIterable<T> implements Iterable<T> {

    private Iterable<Iterable<T>> children;

    public FlatteningIterable(Iterable<Iterable<T>> children) {
        this.children = children;
    }

    public FlatteningIterable(Iterable<T>... children) {
        this.children = Arrays.asList(children);
    }

    @Override
    public Iterator<T> iterator() {
        return new FlatteningIterator<>(new MappingIterator<>(this.children.iterator(), Iterable::iterator));
    }

}
