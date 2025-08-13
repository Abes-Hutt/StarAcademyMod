package abeshutt.staracademy.attribute.again.type;

import java.util.ArrayList;
import java.util.List;

public abstract class AttributeType<T> {

    protected AttributeType() {

    }

    public abstract int toBitMask();

    public AttributeType<?> simplify() {
        int mask = this.toBitMask();
        List<AttributeType<?>> parts = new ArrayList<>();

        for(int i = 0; i < 32; i++) {
            int child = 1 << i;

            if((mask & child) != 0) {
                parts.add(switch(child) {
                    case NumberAttributeType.MASK -> AttributeTypes.number();
                    default -> AttributeTypes.never();
                });
            }
        }

        if(parts.isEmpty()) {
            return AttributeTypes.never();
        } else if(parts.size() == 1) {
            return parts.getFirst();
        }

        return AttributeTypes.union(parts.toArray(new AttributeType[0]));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AttributeType<?> other) {
            return this.toBitMask() == other.toBitMask();
        }

        return false;
    }

}
