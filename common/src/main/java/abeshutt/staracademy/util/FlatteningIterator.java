package abeshutt.staracademy.util;

import java.util.Arrays;
import java.util.Iterator;

public class FlatteningIterator<T> implements Iterator<T> {

    private Iterator<Iterator<T>> children;
    private Iterator<T> current;

    public FlatteningIterator(Iterator<Iterator<T>> children) {
        this.children = children;
    }

    public FlatteningIterator(Iterator<T>... children) {
        this.children = Arrays.asList(children).iterator();
    }

    @Override
    public boolean hasNext() {
        this.compute();
        return this.current.hasNext();
    }

    @Override
    public T next() {
        this.compute();
        return this.current.next();
    }

    protected void compute() {
        while((this.current == null || !this.current.hasNext()) && this.children.hasNext()) {
            this.current = this.children.next();
        }
    }

}