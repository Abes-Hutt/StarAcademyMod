package abeshutt.staracademy.attribute;

import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;

import java.util.Collection;

import static abeshutt.staracademy.attribute.NaryModifier.constant;

public class ListAttribute<E> extends Attribute<Collection<E>> {

    public static <E> Modifier<Collection<E>> assign(Collection<E> value) {
        return UnaryModifier.projection("assign",
                constant("value", value, null));
    }

    @Override
    protected TypeSupplierAdapter<Modifier<Collection<E>>> getModifierAdapter() {
        return null;
    }

}
