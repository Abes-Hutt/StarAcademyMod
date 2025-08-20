package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.attribute.again.type.NumberAttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.math.Rational;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;
import java.util.function.Consumer;

public class AddAttribute<T> extends Attribute<T> {

    private Attribute<T> value;

    protected AddAttribute(AttributeType<T> type) {
        super(type);
    }

    public AddAttribute(AttributeType<T> type, Attribute<T> value) {
        super(type);
        this.value = value;
    }

    public Attribute<T> getValue() {
        return this.value;
    }

    @Override
    public Option<T> get(Option<T> value, AttributeContext context) {
        if(value.isAbsent()) {
            return Option.absent();
        }

        Option<T> operand = this.value.get(Option.absent(), context);

        if(operand.isAbsent()) {
            return value;
        }

        if(this.type instanceof NumberAttributeType) {
            Rational a = (Rational)value.get();
            Rational b = (Rational)operand.get();
            return Option.present((T)a.add(b));
        }

        return value;
    }

    @Override
    public void populate(AttributeContext context) {
        this.value.populate(context);
        super.populate(context);
    }

    public void iterate(Consumer<Attribute<?>> action) {
        super.iterate(action);
        this.value.iterate(action);
    }

    @Override
    public Optional<NbtElement> writeNbt() {
        return super.writeNbt().map(nbt -> {
            if(nbt instanceof NbtCompound compound) {
                Adapters.ATTRIBUTE.writeNbt(this.value, this.type)
                        .ifPresent(tag -> compound.put("value", tag));
            }

            return nbt;
        });
    }

    @Override
    public void readNbt(NbtElement nbt) {
        super.readNbt(nbt);

        if(!(nbt instanceof NbtCompound compound)) {
            throw new UnsupportedOperationException();
        }

        this.value = (Attribute<T>)Adapters.ATTRIBUTE.readNbt(compound.get("value"), this.type).orElseThrow();
        this.value.setParent(this);
    }

}
