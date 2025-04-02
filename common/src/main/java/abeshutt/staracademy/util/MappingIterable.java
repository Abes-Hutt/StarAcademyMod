package abeshutt.staracademy.util;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterable<INPUT, OUTPUT> implements Iterable<OUTPUT> {

    private final Iterable<INPUT> parent;
    private final Function<INPUT, OUTPUT> mapper;

    public MappingIterable(Iterable<INPUT> parent, Function<INPUT, OUTPUT> mapper) {
        this.parent = parent;
        this.mapper = mapper;
    }

    @Override
    public Iterator<OUTPUT> iterator() {
        return new MappingIterator<>(this.parent.iterator(), this.mapper);
    }

}
