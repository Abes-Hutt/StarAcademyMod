package abeshutt.staracademy.attribute.type;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.math.Rational;

public class NumberAttributeType extends AttributeType<Rational> {

    @Override
    public TypeSupplierAdapter<Attribute<Rational>> getModifiers() {
        return Modifiers.INSTANCE;
    }

    protected static class Modifiers extends AttributeType.Modifiers<Rational> {
        private static final Modifiers INSTANCE = new Modifiers();

        public Modifiers() {
            //this.register("add", AddAttribute.class, AddAttribute::new);
        }
    }

}
