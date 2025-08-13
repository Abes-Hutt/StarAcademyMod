package abeshutt.staracademy.attribute.modifier;

import abeshutt.staracademy.attribute.Attribute;
import abeshutt.staracademy.attribute.ModifierInstance;
import abeshutt.staracademy.attribute.parent.AttributeParent;
import abeshutt.staracademy.attribute.parent.ModifierAttributeParent;
import abeshutt.staracademy.attribute.type.AttributeType;
import abeshutt.staracademy.attribute.type.NeverAttributeType;

public abstract class LazyAttribute<T> extends Attribute<Object> {

    private T serialized;
    private Attribute<?> delegate;

    protected LazyAttribute(T serialized) {
        super(new NeverAttributeType());
        this.serialized = serialized;
        this.delegate = null;
    }

    @Override
    public void setParent(AttributeParent parent) {
        super.setParent(parent);

        if(parent != null) {
            this.assign(parent.get().getType());
        } else {
            this.assign(null);
        }
    }

    public void assign(AttributeType<?> type) {
        if(this.type.equals(type)) {
            return;
        }

        if(this.delegate != null) {
            this.serialized = this.encode(this.type, this.delegate);
            this.delegate.setParent(null);
            this.delegate.clear();
        }

        this.type = (AttributeType)type;
        this.delegate = this.decode(this.type, this.serialized);

        if(this.delegate != null) {
            this.delegate.setParent(this.parent);
        }

        Attribute<?> self = this.delegate == null ? this : this.delegate;

        for(int i = 0; i < this.orderedModifiers.size(); i++) {
            ModifierInstance<Object> modifier = this.orderedModifiers.get(i);
            modifier.get().setParent(new ModifierAttributeParent(self, i));
        }
    }

    protected abstract T encode(AttributeType<?> type, Attribute<?> attribute);

    protected abstract Attribute<?> decode(AttributeType<?> type, T serialized);

}
