package abeshutt.staracademy.attribute.again.type;

public class AttributeTypes {

    public static <T> NeverAttributeType<T> never() {
        return new NeverAttributeType<>();
    }

    public static NumberAttributeType number() {
        return new NumberAttributeType();
    }

    public static UnionAttributeType<?> union(AttributeType<?>... types) {
        return new UnionAttributeType<>(types);
    }

    public static IntersectionAttributeType<?> intersection(AttributeType<?>... types) {
        return new IntersectionAttributeType<>(types);
    }

    public static AnyAttributeType<?> any() {
        return new AnyAttributeType<>();
    }

}
