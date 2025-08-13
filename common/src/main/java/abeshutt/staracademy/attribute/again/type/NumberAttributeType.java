package abeshutt.staracademy.attribute.again.type;

import abeshutt.staracademy.math.Rational;

public class NumberAttributeType extends AttributeType<Rational> {

    public static final int MASK = 1;

    protected NumberAttributeType() {

    }

    @Override
    public int toBitMask() {
        return MASK;
    }

}
