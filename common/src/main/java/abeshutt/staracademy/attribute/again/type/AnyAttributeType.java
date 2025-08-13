package abeshutt.staracademy.attribute.again.type;

public class AnyAttributeType<T> extends AttributeType<T> {

    @Override
    public int toBitMask() {
        return NumberAttributeType.MASK;
    }

}
