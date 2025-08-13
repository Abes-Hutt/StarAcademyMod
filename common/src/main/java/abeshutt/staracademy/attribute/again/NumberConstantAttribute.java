package abeshutt.staracademy.attribute.again;

import abeshutt.staracademy.attribute.Option;
import abeshutt.staracademy.attribute.again.type.AttributeType;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.roll.NumberRoll;

import static abeshutt.staracademy.attribute.again.type.AttributeTypes.number;

public class NumberConstantAttribute extends ValueAttribute<Rational, Rational, NumberRoll> {

    protected NumberConstantAttribute() {
        this(number(), null, null);
    }

    protected NumberConstantAttribute(AttributeType<Rational> type, Rational value, NumberRoll config) {
        super(type, value, config, Adapters.RATIONAL, Adapters.NUMBER_ROLL);
    }

    public static NumberConstantAttribute of(NumberRoll config) {
        return new NumberConstantAttribute(number(), null, config);
    }

    @Override
    protected Option<Rational> compute(Option<Rational> value, AttributeContext context) {
        return this.value == null ? Option.absent() : Option.present(this.value);
    }

    @Override
    protected void generate(AttributeContext context) {
        this.value = this.getConfig().get(context.getRandom());
    }

}
