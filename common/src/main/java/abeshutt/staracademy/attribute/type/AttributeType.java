package abeshutt.staracademy.attribute.type;

import abeshutt.staracademy.attribute.Modifier;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;

public abstract class AttributeType<T> {

    protected abstract TypeSupplierAdapter<Modifier<T>> getModifierAdapter();

}
