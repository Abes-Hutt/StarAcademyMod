package abeshutt.staracademy.attribute.again.type;

public class NeverAttributeType<T> extends AttributeType<T> {

    public static final int MASK = 0;

    protected NeverAttributeType() {

    }

    @Override
    public int toBitMask() {
        return MASK;
    }

}
