package abeshutt.staracademy.attribute;

import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;

import java.util.Collection;

import static abeshutt.staracademy.attribute.NaryAttributeModifier.constant;

public class ListAttribute<E> extends Attribute<Collection<E>> {

    public static <E> AttributeModifier<Collection<E>> assign(Collection<E> value) {
        return UnaryAttributeModifier.projection("assign",
                constant("value", value, null));
    }

    @Override
    protected TypeSupplierAdapter<AttributeModifier<Collection<E>>> getModifierAdapter() {
        return null;
    }

}
