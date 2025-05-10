package abeshutt.staracademy.attribute.type;

import java.util.Collection;

public abstract class ListAttributeType<E> extends AttributeType<Collection<E>> {

    private final AttributeType<E> element;

    public ListAttributeType(AttributeType<E> element) {
        this.element = element;
    }

    public AttributeType<E> getElement() {
        return this.element;
    }

}
