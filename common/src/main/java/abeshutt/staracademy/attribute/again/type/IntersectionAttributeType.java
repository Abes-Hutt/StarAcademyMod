package abeshutt.staracademy.attribute.again.type;

public class IntersectionAttributeType<T> extends AttributeType<T> {

    private final AttributeType<?>[] types;

    protected IntersectionAttributeType(AttributeType<?>... types) {
        this.types = types;
    }

    @Override
    public int toBitMask() {
        int mask = ~0;

        for(AttributeType<?> type : this.types) {
            mask = mask & type.toBitMask();
        }

        return mask;
    }

}
