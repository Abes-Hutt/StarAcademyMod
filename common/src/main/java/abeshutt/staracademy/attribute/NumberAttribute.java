package abeshutt.staracademy.attribute;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.basic.TypeSupplierAdapter;
import abeshutt.staracademy.math.Rational;

import static abeshutt.staracademy.attribute.NaryAttributeModifier.constant;

public class NumberAttribute extends Attribute<Rational> {

    public static AttributeModifier<Rational> assign(Number value) {
        return UnaryAttributeModifier.projection("assign",
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static AttributeModifier<Rational> invert() {
        return NullaryAttributeModifier.arithmetic("invert", Rational::invert);
    }

    public static AttributeModifier<Rational> add(Number value) {
        return UnaryAttributeModifier.arithmetic("add", Rational::add,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static AttributeModifier<Rational> subtract(Number value) {
        return UnaryAttributeModifier.arithmetic("subtract", Rational::subtract,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static AttributeModifier<Rational> multiply(Number value) {
        return UnaryAttributeModifier.arithmetic("multiply", Rational::multiply,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static AttributeModifier<Rational> divide(Number value) {
        return UnaryAttributeModifier.arithmetic("divide", Rational::divide,
                constant("value", Rational.of(value), Adapters.RATIONAL));
    }

    public static AttributeModifier<Rational> power(int value) {
        return UnaryAttributeModifier.arithmetic("power", Rational::pow,
                constant("value", value, Adapters.INT));
    }

    public static AttributeModifier<Rational> clamp(Number min, Number max) {
        return BinaryAttributeModifier.arithmetic("clamp", Rational::clamp,
                constant("min", Rational.of(min), Adapters.RATIONAL),
                constant("max", Rational.of(max), Adapters.RATIONAL));
    }

    @Override
    protected TypeSupplierAdapter<AttributeModifier<Rational>> getModifierAdapter() {
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
