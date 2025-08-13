package abeshutt.staracademy.attribute.type;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;

public abstract class AttributeType<T> {

    public abstract TypeSupplierAdapter<Attribute<T>> getModifiers();

    protected static class Modifiers<T> extends TypeSupplierAdapter<Attribute<T>> {
        public Modifiers() {
            super("type", true);
        }
    }

}
