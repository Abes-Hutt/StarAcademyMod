package abeshutt.staracademy.attribute.modifier;

import abeshutt.staracademy.attribute.path.AttributePath;
import abeshutt.staracademy.math.Rational;
import abeshutt.staracademy.world.roll.NumberRoll;

public class AddAttribute extends OperandNumberAttribute {

    protected AddAttribute(Config config) {
        super(config, Rational::add);
    }

    public static AddAttribute empty() {
        return new AddAttribute(new Config());
    }

    public static AddAttribute of(Rational value) {
        return of(NumberRoll.constant(value));
    }

    public static AddAttribute of(NumberRoll roll) {
        return new AddAttribute(new Config(roll, null));
    }

    public static AddAttribute of(AttributePath<Rational> path) {
        return new AddAttribute(new Config(null, path));
    }

}
