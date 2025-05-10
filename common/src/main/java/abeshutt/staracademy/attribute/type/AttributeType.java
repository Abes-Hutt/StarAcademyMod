package abeshutt.staracademy.attribute.type;

import abeshutt.staracademy.attribute.AttributeModifier;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;

public abstract class AttributeType<T> {

    protected abstract TypeSupplierAdapter<AttributeModifier<T>> getModifierAdapter();

}
