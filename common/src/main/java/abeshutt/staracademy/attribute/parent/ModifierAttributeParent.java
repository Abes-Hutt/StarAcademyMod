package abeshutt.staracademy.attribute.parent;

import abeshutt.staracademy.attribute.Attribute;

public class ModifierAttributeParent extends AttributeParent {

    private final int index;

    public ModifierAttributeParent(Attribute<?> parent, int index) {
        super(parent);
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

}
