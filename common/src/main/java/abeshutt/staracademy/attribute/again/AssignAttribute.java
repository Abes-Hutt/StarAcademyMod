package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.roll.NumberRoll;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;
import java.util.function.Consumer;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.number;

public class AssignAttribute<T> extends Attribute<T> {

    private Attribute<T> value;

    protected AssignAttribute(AttributeType<T> type) {
        super(type);
    }

    protected AssignAttribute(AttributeType<T> type, Attribute<T> value) {
        super(type);
        this.value = value;
    }

    public static AssignAttribute<Rational> of(NumberRoll roll) {
        return new AssignAttribute<>(number(), NumberConstantAttribute.of(roll));
    }

    public Attribute<T> getValue() {
        return this.value;
    }

    @Override
    public Option<T> get(Option<T> value, AttributeContext context) {
        Option<T> result = this.value.get(Option.absent(), context);

        if(result.isAbsent()) {
            return value;
        }

        return result;
    }

    @Override
    public void populate(AttributeContext context) {
        this.value.populate(context);
        super.populate(context);
    }

    public void iterate(Consumer<Attribute<?>> action) {
        super.iterate(action);
        action.accept(this.value);
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
