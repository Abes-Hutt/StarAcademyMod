package abeshutt.staracademy.card;

import abeshutt.staracademy.math.Rational;

import java.math.RoundingMode;

public class DecimalPercentageAttributeStyle extends DecimalAttributeStyle {

    private static final Rational _100 = Rational.of(100);

    protected DecimalPercentageAttributeStyle() {

    }

    public DecimalPercentageAttributeStyle(int scale, RoundingMode rounding, boolean stripped) {
        super(scale, rounding, stripped);
    }

    @Override
    public String format(Rational rational) {
        return super.format(rational.multiply(_100)) + "%";
    }

}
