package abeshutt.staracademy.attribute;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.math.Rational;

import static abeshutt.staracademy.attribute.NaryModifier.constant;

public class NumberAttribute extends Attribute<Rational> {

    public static Modifier<Rational> assign(Number value) {
        return UnaryModifier.projection("assign",
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static Modifier<Rational> invert() {
        return NullaryModifier.arithmetic("invert", Rational::invert);
    }

    public static Modifier<Rational> add(Number value) {
        return UnaryModifier.arithmetic("add", Rational::add,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static Modifier<Rational> subtract(Number value) {
        return UnaryModifier.arithmetic("subtract", Rational::subtract,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static Modifier<Rational> multiply(Number value) {
        return UnaryModifier.arithmetic("multiply", Rational::multiply,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static Modifier<Rational> divide(Number value) {
        return UnaryModifier.arithmetic("divide", Rational::divide,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static Modifier<Rational> power(int value) {
        return UnaryModifier.arithmetic("power", Rational::pow,
                constant("value", value, Adapters.INT));
    }

    public static Modifier<Rational> clamp(Number min, Number max) {
        return BinaryModifier.arithmetic("clamp", Rational::clamp,
                constant("min", Rational.of(min), Adapters.RATIONAL),
                constant("max", Rational.of(max), Adapters.RATIONAL));
    }

    @Override
    protected TypeSupplierAdapter<Modifier<Rational>> getModifierAdapter() {
        return ModifierAdapter.INSTANCE;
    }

    protected static class ModifierAdapter extends Attribute.ModifierAdapter<Rational> {
        protected static final ModifierAdapter INSTANCE = new ModifierAdapter();

        public ModifierAdapter() {
            this.register(() -> NumberAttribute.assign(0));
            this.register(NumberAttribute::invert);
            this.register(() -> NumberAttribute.add(0));
            this.register(() -> NumberAttribute.subtract(0));
            this.register(() -> NumberAttribute.multiply(0));
            this.register(() -> NumberAttribute.divide(0));
            this.register(() -> NumberAttribute.power(0));
            this.register(() -> NumberAttribute.clamp(0, 0));
        }
    }

}
