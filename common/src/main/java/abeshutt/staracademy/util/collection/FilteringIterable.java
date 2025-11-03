package abeshutt.staracademy.util.collection;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilteringIterable<INPUT> implements Iterable<INPUT> {

    private final Iterable<INPUT> parent;
    private final Predicate<INPUT> filter;

    public FilteringIterable(Iterable<INPUT> parent, Predicate<INPUT> filter) {
        this.parent = parent;
        this.filter = filter;
    }

    @Override
    public Iterator<INPUT> iterator() {
        return new FilteringIterator<>(this.parent.iterator(), this.filter);
    }

}
