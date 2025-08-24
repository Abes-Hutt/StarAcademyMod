package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.data.adapter.Adapters;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public class ReferenceAttribute<T> extends Attribute<T> {

    private AttributePath<T> path;

    protected ReferenceAttribute(AttributeType<T> type, AttributePath<T> path) {
        super(type);
        this.path = path;
    }

    public static <T> ReferenceAttribute<T> of(AttributeType<T> type, AttributePath<T> path) {
        return new ReferenceAttribute<T>(type, path);
    }

    @Override
    public Option<T> get(Option<T> value, AttributeContext context) {
        if(this.path.isEmpty()) {
            return value;
        }

        Attribute<T> target = this.path(this.path).orElse(null);

        if(target == null) {
            return value;
        }

        return target.get(value, context);
    }

    @Override
    public Optional<NbtElement> writeNbt() {
        return Adapters.ATTRIBUTE_PATH.writeNbt(this.path);
    }

    @Override
    public void readNbt(NbtElement nbt) {
        this.path = Adapters.ATTRIBUTE_PATH.readNbt(nbt).orElseGet(AttributePath::empty);
    }

}
