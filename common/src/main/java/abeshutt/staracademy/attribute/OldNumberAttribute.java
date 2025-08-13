package abeshutt.staracademy.attribute;

import abeshutt.staracademy.attribute.type.AttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.math.Rational;

import static abeshutt.staracademy.attribute.NaryModifier.constant;

public class OldNumberAttribute extends Attribute<Rational> {

    protected OldNumberAttribute(AttributeType<Rational> type) {
        super(type);
    }

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
                constant("minimum", Rational.of(min), Adapters.RATIONAL),
                constant("maximum", Rational.of(max), Adapters.RATIONAL));
    }

    /*
    protected TypeSupplierAdapter<Attribute<Rational>> getModifierAdapter() {
        return ModifierAdapter.INSTANCE;
    }

    protected static class ModifierAdapter extends Attribute.ModifierAdapter<Rational> {
        protected static final ModifierAdapter INSTANCE = new ModifierAdapter();

        public ModifierAdapter() {
            this.register(() -> OldNumberAttribute.assign(0));
            this.register(OldNumberAttribute::invert);
            this.register(() -> OldNumberAttribute.add(0));
            this.register(() -> OldNumberAttribute.subtract(0));
            this.register(() -> OldNumberAttribute.multiply(0));
            this.register(() -> OldNumberAttribute.divide(0));
            this.register(() -> OldNumberAttribute.power(0));
            this.register(() -> OldNumberAttribute.clamp(0, 0));
        }
    }*/

}
