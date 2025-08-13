package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.intersection;

public class LazyAttribute<T> extends Attribute<T> {

    private NbtElement serialized;
    private Attribute<T> delegate;

    protected LazyAttribute(AttributeType<T> type) {
        super(type);
    }

    public LazyAttribute<T> setSerialized(NbtElement serialized) {
        this.serialized = serialized;
        return this;
    }

    @Override
    protected void narrow(AttributeType<T> type) {
        AttributeType<?> merged = intersection(type, this.type).simplify();
        //this.delegate = (Attribute<T>)this.parse(type, this.serialized);
        //this.type = merged;
        super.narrow(type);
    }

    @Override
    public Option<T> get(Option<T> value, AttributeContext context) {
        if(this.delegate == null) {
            return value;
        }

        return this.delegate.get(value, context);
    }

    @Override
    public Optional<NbtElement> writeNbt() {
        return Optional.of(this.serialized);
    }

    @Override
    public void readNbt(NbtElement nbt) {
        this.serialized = nbt;
    }

}
