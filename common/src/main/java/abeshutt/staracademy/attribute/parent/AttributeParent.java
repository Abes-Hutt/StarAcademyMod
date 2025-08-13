package abeshutt.staracademy.attribute.parent;

import abeshutt.staracademy.attribute.Attribute;

public class AttributeParent {

    private final Attribute<?> parent;

    public AttributeParent(Attribute<?> parent) {
        this.parent = parent;
    }

    public Attribute<?> get() {
        return this.parent;
    }

}
