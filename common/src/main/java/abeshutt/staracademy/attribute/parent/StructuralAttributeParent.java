package abeshutt.staracademy.attribute.parent;

import abeshutt.staracademy.attribute.Attribute;

public class StructuralAttributeParent extends AttributeParent {

    private final String id;

    public StructuralAttributeParent(Attribute<?> parent, String id) {
        super(parent);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}
