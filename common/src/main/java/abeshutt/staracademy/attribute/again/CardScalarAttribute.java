package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.attribute.again.type.AttributeTypes;
import abeshutt.staracademy.attribute.again.type.NumberAttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.math.Rational;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardScalarAttribute<T> extends Attribute<T> {

    private String id;
    private int grade;

    protected CardScalarAttribute(AttributeType<T> type) {
        super(type);
    }

    protected CardScalarAttribute(AttributeType<T> type, String id) {
        super(type);
        this.id = id;
    }

    public static CardScalarAttribute<Rational> of(String id) {
        return new CardScalarAttribute<>(AttributeTypes.number(), id);
    }

    public CardScalarAttribute<T> setGrade(int grade) {
        this.grade = grade;
        return this;
    }

    @Override
    public Option<T> get(Option<T> value, AttributeContext context) {
        if(value.isAbsent()) {
            return Option.absent();
        }

        List<Rational> scalars = ModConfigs.CARD_SCALARS.get(this.id).orElse(new ArrayList<>());
        int index = this.grade - 1;

        if(index <= 0 || index >= scalars.size()) {
            return value;
        }

        if(this.type instanceof NumberAttributeType) {
            Rational a = (Rational)value.get();
            Rational b = scalars.get(index);
            return Option.present((T)a.multiply(b));
        }

        return value;
    }

    @Override
    public Optional<NbtElement> writeNbt() {
        return super.writeNbt().map(nbt -> {
            if(nbt instanceof NbtCompound compound) {
                Adapters.UTF_8.writeNbt(this.id).ifPresent(tag -> compound.put("id", tag));
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

        this.id = Adapters.UTF_8.readNbt(compound.get("id")).orElseThrow();
    }

}
