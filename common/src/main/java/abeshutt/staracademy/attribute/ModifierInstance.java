package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.parent.AttributeParent;

public class ModifierInstance<T> {

    private final Attribute<T> parent;
    private final Object owner;
    private final ModifierReference<T> reference;
    private Attribute<T> instance;

    public ModifierInstance(Attribute<T> parent, Object owner, ModifierReference<T> reference) {
        this.parent = parent;
        this.owner = owner;
        this.reference = reference;
        this.reference.addChangeListener(this, this::update);
        this.update();
    }

    public Object getOwner() {
        return this.owner;
    }

    public ModifierReference<T> getReference() {
        return this.reference;
    }

    public Attribute<T> get() {
        return this.instance;
    }

    public boolean isRemoved() {
        return this.reference.isRemoved();
    }

    protected void update() {
        AttributeParent current = this.instance.getParent();
        this.instance = this.reference.get().copy();
        this.instance.setParent(current);
        this.parent.invalidate();
    }

    public void dispose() {
        this.reference.removeChangeListener(this);
        this.instance.setParent(null);
    }

}
