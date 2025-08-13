package abeshutt.staracademy.attribute.type;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;

public class NeverAttributeType extends AttributeType<Object> {

    @Override
    public TypeSupplierAdapter<Attribute<Object>> getModifiers() {
        return null;
    }

}
