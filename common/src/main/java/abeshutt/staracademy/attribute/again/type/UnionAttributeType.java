package abeshutt.staracademy.attribute.again.type;

public class UnionAttributeType<T> extends AttributeType<T> {

    private final AttributeType<?>[] types;

    protected UnionAttributeType(AttributeType<?>... types) {
        this.types = types;
    }

    @Override
    public int toBitMask() {
        int mask = 0;

        for(AttributeType<?> type : this.types) {
            mask = mask | type.toBitMask();
        }

        return mask;
    }

}
