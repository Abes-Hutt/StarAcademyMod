package abeshutt.staracademy.attribute;

public class AttributeParent {

    private final Attribute<?> parent;
    private final int index;

    public AttributeParent(Attribute<?> parent, int index) {
        this.parent = parent;
        this.index = index;
    }

    public Attribute<?> get() {
        return this.parent;
    }

    public int getIndex() {
        return this.index;
    }

}
