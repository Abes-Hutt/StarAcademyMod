package abeshutt.staracademy.attribute.type;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.Modifier;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;

public abstract class AttributeType<T> {

    public abstract TypeSupplierAdapter<Attribute<T>> getModifierAdapter();

}
