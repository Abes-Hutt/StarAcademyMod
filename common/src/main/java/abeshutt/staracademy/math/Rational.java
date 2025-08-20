package abeshutt.staracademy.math;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.regex.Pattern;

public class Rational extends Number implements Comparable<Rational> {

    protected static final BigInteger THRESHOLD = BigInteger.ONE.shiftLeft(128);

    public static final Rational ZERO = Rational.of(0, 1);
    public static final Rational HALF = Rational.of(1, 2);
    public static final Rational ONE = Rational.of(1, 1);
    public static final Rational TWO = Rational.of(2, 1);
    public static final Rational FLOAT_EPSILON = Rational.of(1, 1 << 24);
    public static final Rational DOUBLE_EPSILON = Rational.of(1, 1L << 53);

    protected BigInteger numerator;
    protected BigInteger denominator;

    protected Rational(BigInteger numerator, BigInteger denominator) {
        if(denominator.signum() == 0) {
            throw new ArithmeticException("/ by zero");
        }

        this.numerator = numerator;
        this.denominator = denominator;

        if(this.numerator.compareTo(THRESHOLD) < 0 && this.denominator.compareTo(THRESHOLD) < 0) {
            this.simplify();
        }
    }

    public BigInteger getNumerator() {
        return this.numerator;
    }

    public BigInteger getDenominator() {
        return this.denominator;
    }

    public Rational simplify() {
        if(this.numerator.signum() == 0) {
            this.denominator = BigInteger.ONE;
            return this;
        } else if(this.denominator.signum() < 0) {
            this.numerator = this.numerator.negate();
            this.denominator = this.denominator.negate();
        }

        BigInteger gcd = this.numerator.gcd(this.denominator);
        this.numerator = this.numerator.divide(gcd);
        this.denominator = this.denominator.divide(gcd);
        return this;
    }

    public Rational abs() {
        return this.getNumerator().signum() < 0 ? this.negate() : this;
    }

    public Rational negate() {
        return Rational.of(this.getNumerator().negate(), this.getDenominator());
    }

    public Rational invert() {
        return Rational.of(this.getDenominator(), this.getNumerator());
    }

    public int signum() {
        return this.getNumerator().signum();
    }

    public Rational min(Rational other) {
        return this.compareTo(other) <= 0 ? this : other;
    }

    public Rational max(Rational other) {
        return this.compareTo(other) >= 0 ? this : other;
    }

    public Rational add(Rational addend) {
        BigInteger a = this.getNumerator().multiply(addend.getDenominator());
        BigInteger b = addend.getNumerator().multiply(this.getDenominator());
        return Rational.of(a.add(b), this.getDenominator().multiply(addend.getDenominator()));
    }

    public Rational add(BigDecimal addend) {
        return this.add(Rational.of(addend));
    }

    public Rational add(BigInteger addend) {
        return Rational.of(this.getNumerator().add(addend.multiply(this.getDenominator())), this.getDenominator());
    }

    public Rational add(double addend) {
        return this.add(Rational.of(addend));
    }

    public Rational add(long addend) {
        return this.add(BigInteger.valueOf(addend));
    }

    public Rational subtract(Rational subtrahend) {
        return this.add(subtrahend.negate());
    }

    public Rational subtract(BigDecimal subtrahend) {
        return this.subtract(Rational.of(subtrahend));
    }

    public Rational subtract(BigInteger subtrahend) {
        return this.add(subtrahend.negate());
    }

    public Rational subtract(double subtrahend) {
        return this.subtract(Rational.of(subtrahend));
    }

    public Rational subtract(long subtrahend) {
        return this.subtract(BigInteger.valueOf(subtrahend));
    }

    public Rational multiply(Rational multiplier) {
        BigInteger a = this.getNumerator().multiply(multiplier.getNumerator());
        BigInteger b = this.getDenominator().multiply(multiplier.getDenominator());
        return Rational.of(a, b);
    }

    public Rational multiply(BigDecimal multiplier) {
        return this.multiply(Rational.of(multiplier));
    }

    public Rational multiply(BigInteger multiplier) {
        return Rational.of(this.getNumerator().multiply(multiplier), this.getDenominator());
    }

    public Rational multiply(double multiplier) {
        return this.multiply(Rational.of(multiplier));
    }

    public Rational multiply(long multiplier) {
        return this.multiply(BigInteger.valueOf(multiplier));
    }

    public Rational divide(Rational divisor) {
        return this.multiply(divisor.invert());
    }

    public Rational divide(BigDecimal divisor) {
        return this.divide(Rational.of(divisor));
    }

    public Rational divide(BigInteger divisor) {
        return Rational.of(this.getNumerator(), this.getDenominator().multiply(divisor));
    }

    public Rational divide(double divisor) {
        return this.divide(Rational.of(divisor));
    }

    public Rational divide(long divisor) {
        return this.divide(BigInteger.valueOf(divisor));
    }

    public Rational pow(BigInteger exponent) {
        return this.pow(exponent.intValueExact());
    }

    public Rational pow(int exponent) {
        BigInteger a = this.getNumerator().pow(exponent);
        BigInteger b = this.getDenominator().pow(exponent);
        return Rational.of(a, b);
    }

    public Rational shiftRight(int n) {
        Rational r = this;
        int i = Math.min(this.getNumerator().getLowestSetBit(), n);
        if(i > 0)r = Rational.of(this.getNumerator().shiftRight(i), this.getDenominator());
        if(n - i > 0)r = Rational.of(r.getNumerator(), r.getDenominator().shiftLeft(n - i));
        return r;
    }

    public Rational shiftLeft(int n) {
        Rational r = this;
        int i = Math.min(this.getDenominator().getLowestSetBit(), n);
        if(i > 0)r = Rational.of(this.getNumerator(), this.getDenominator().shiftRight(i));
        if(n - i > 0)r = Rational.of(r.getNumerator().shiftLeft(n - i), r.getDenominator());
        return r;
    }

    public Rational floor() {
        if(this.getDenominator().equals(BigInteger.ONE)) {
            return this;
        }

        BigInteger[] divMod = this.getNumerator().divideAndRemainder(this.getDenominator());
        BigInteger quotient = divMod[0];
        BigInteger remainder = divMod[1];

        if(!remainder.equals(BigInteger.ZERO) && this.signum() < 0) {
            quotient = quotient.subtract(BigInteger.ONE);
        }

        return Rational.of(quotient);
    }

    public Rational ceil() {
        if(this.getDenominator().equals(BigInteger.ONE)) {
            return this;
        }

        BigInteger[] divMod = this.getNumerator().divideAndRemainder(this.getDenominator());
        BigInteger quotient = divMod[0];
        BigInteger remainder = divMod[1];

        if(!remainder.equals(BigInteger.ZERO) && this.signum() > 0) {
            quotient = quotient.add(BigInteger.ONE);
        }

        return Rational.of(quotient);
    }

    public Rational round() {
        if(this.getNumerator().signum() >= 0) {
            return this.add(HALF).floor();
        } else {
            return this.subtract(HALF).ceil();
        }
    }

    @Override
    public int intValue() {
        return this.getNumerator().divide(this.getDenominator()).intValue();
    }

    @Override
    public long longValue() {
        return this.getNumerator().divide(this.getDenominator()).longValue();
    }

    @Override
    public float floatValue() {
        BigDecimal a = new BigDecimal(this.getNumerator());
        BigDecimal b = new BigDecimal(this.getDenominator());
        return a.divide(b, MathContext.DECIMAL32).floatValue();
    }

    @Override
    public double doubleValue() {
        BigDecimal a = new BigDecimal(this.getNumerator());
        BigDecimal b = new BigDecimal(this.getDenominator());
        return a.divide(b, MathContext.DECIMAL64).doubleValue();
    }

    public BigInteger toBigInteger() {
        return this.getNumerator().divide(this.getDenominator());
    }

    public BigDecimal toBigDecimal(int scale, RoundingMode roundingMode) {
        return new BigDecimal(this.getNumerator())
                .divide(new BigDecimal(this.getDenominator()), scale, roundingMode);
    }

    @Override
    public int compareTo(Rational other) {
        BigInteger a = this.getNumerator().multiply(other.getDenominator());
        BigInteger b = this.getDenominator().multiply(other.getNumerator());
        return a.compareTo(b);
    }

    @Override
    public int hashCode() {
        return this.getNumerator().hashCode() + 31 * this.getDenominator().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(other == this)return true;
        if(!(other instanceof Rational))return false;
        return this.compareTo((Rational)other) == 0;
    }

    @Override
    public String toString() {
        Rational r = this.simplify();
        return r.signum() == 0 || r.getDenominator().equals(BigInteger.ONE)
                ? r.getNumerator().toString() : r.getNumerator() + " / " + r.getDenominator();
    }

    public String toString(int scale) {
        return this.toBigDecimal(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    public static Rational of(BigInteger numerator, BigInteger denominator) {
        return new Rational(numerator, denominator);
    }

    public static Rational of(Number numerator, Number denominator) {
        return Rational.of(numerator).divide(Rational.of(denominator));
    }

    public static Rational of(BigDecimal value) {
        value = value.stripTrailingZeros();
        int scale = value.scale();
        BigInteger numerator = value.unscaledValue();
        BigInteger denominator;

        if(scale >= 0) {
            denominator = BigInteger.TEN.pow(scale);
        } else {
            numerator = numerator.multiply(BigInteger.TEN.pow(-scale));
            denominator = BigInteger.ONE;
        }

        return of(numerator, denominator);
    }

    public static Rational of(BigInteger value) {
        return of(value, BigInteger.ONE);
    }

    public static Rational of(double value) {
        if(Double.isNaN(value) || Double.isInfinite(value)) {
            throw new ArithmeticException("Value " + value + " is not a rational");
        }

        if(value == 0.0D) {
            return Rational.ZERO;
        }

        long bits = Double.doubleToRawLongBits(value);
        long sign = bits >>> 63;
        int exponent = (int)(bits >>> 52) & 0x7FF;
        long mantissa = bits & 0xFFFFFFFFFFFFFL;

        if(exponent == 0) {
            exponent = -1022;
        } else {
            mantissa |= (1L << 52);
            exponent -= 1023;
        }

        long a = (sign == 1) ? -mantissa : mantissa;
        int b = exponent - 52;

        if(b == 0) {
            return Rational.of(a, 1);
        } else if(b < 0) {
            return Rational.of(a, BigInteger.ONE.shiftLeft(-b));
        } else {
            return Rational.of(BigInteger.valueOf(a).shiftLeft(b), 1);
        }
    }

    public static Rational of(long value) {
        return of(BigInteger.valueOf(value), BigInteger.ONE);
    }

    public static Rational of(Number number) {
        if(number instanceof BigDecimal decimal) {
            return of(decimal);
        } else if(number instanceof BigInteger integer) {
            return of(integer);
        } else if(number instanceof Float || number instanceof Double) {
            return of(number.doubleValue());
        } else if(number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long) {
            return of(number.longValue());
        } else if(number instanceof Rational rational) {
            return rational;
        }

        throw new UnsupportedOperationException("Cannot convert " + number + " to a rational");
    }

    public static Rational clamp(Rational value, Number min, Number max) {
        Rational a = Rational.of(min);
        if(value.compareTo(a) < 0) return a;
        Rational b = Rational.of(max);
        if(value.compareTo(b) > 0) return b;
        return value;
    }

    public static class Adapter implements ISimpleAdapter<Rational, NbtElement, JsonElement> {
        private final boolean nullable;

        public Adapter(boolean nullable) {
            this.nullable = nullable;
        }

        public boolean isNullable() {
            return this.nullable;
        }

        public Adapter asNullable() {
            return new Adapter(true);
        }

        @Override
        public Optional<NbtElement> writeNbt(Rational value) {
            Rational reduced = value.simplify();

            if(reduced.getDenominator().compareTo(BigInteger.ONE) == 0) {
                return Adapters.BIG_INTEGER.writeNbt(reduced.getNumerator());
            }

            BigInteger remainder = reduced.getDenominator();
            remainder = remainder.shiftRight(remainder.getLowestSetBit());

            BigInteger five = BigInteger.valueOf(5);

            while(remainder.mod(five).equals(BigInteger.ZERO)) {
                remainder = remainder.divide(BigInteger.valueOf(5));
            }

            if(remainder.compareTo(BigInteger.ONE) == 0) {
                BigDecimal decimal = new BigDecimal(reduced.getNumerator())
                        .divide(new BigDecimal(reduced.getDenominator()), MathContext.UNLIMITED);
                return Adapters.BIG_DECIMAL.writeNbt(decimal);
            }

            return Adapters.UTF_8.writeNbt(reduced.getNumerator().toString()
                    + " / " + reduced.getDenominator().toString());
        }

        @Override
        public Optional<Rational> readNbt(NbtElement nbt) {
            if(nbt instanceof NbtString string) {
                String[] parts = string.asString().split(Pattern.quote("/"));

                if(parts.length == 1) {
                    return Optional.of(Rational.of(
                            new BigInteger(parts[0].strip()),
                            BigInteger.ONE));
                } else if(parts.length == 2) {
                    return Optional.of(Rational.of(
                            new BigInteger(parts[0].strip()),
                            new BigInteger(parts[1].strip())));
                }
            }

            return Adapters.BIG_DECIMAL.readNbt(nbt).map(Rational::of);
        }

        @Override
        public Optional<JsonElement> writeJson(Rational value) {
            Rational reduced = value.simplify();

            if(reduced.getDenominator().compareTo(BigInteger.ONE) == 0) {
                return Adapters.BIG_INTEGER.writeJson(reduced.getNumerator());
            }

            BigInteger remainder = reduced.getDenominator();
            remainder = remainder.shiftRight(remainder.getLowestSetBit());

            BigInteger five = BigInteger.valueOf(5);

            while(remainder.mod(five).equals(BigInteger.ZERO)) {
                remainder = remainder.divide(BigInteger.valueOf(5));
            }

            if(remainder.compareTo(BigInteger.ONE) == 0) {
                BigDecimal decimal = new BigDecimal(reduced.getNumerator())
                        .divide(new BigDecimal(reduced.getDenominator()), MathContext.UNLIMITED);
                return Adapters.BIG_DECIMAL.writeJson(decimal);
            }

            return Adapters.UTF_8.writeJson(reduced.getNumerator().toString()
                    + " / " + reduced.getDenominator().toString());
        }

        @Override
        public Optional<Rational> readJson(JsonElement json) {
            if(json instanceof JsonPrimitive primitive) {
                if(primitive.isNumber()) {
                    return Optional.of(Rational.of(primitive.getAsBigDecimal()));
                } else if(primitive.isString()) {
                    String[] parts = primitive.getAsString().split(Pattern.quote("/"));

                    if(parts.length == 1) {
                        return Optional.of(Rational.of(new BigDecimal(parts[0].strip())));
                    } else if(parts.length == 2) {
                        return Optional.of(Rational.of(
                                new BigInteger(parts[0].strip()),
                                new BigInteger(parts[1].strip())));
                    }
                }
            }

            return Optional.empty();
        }
    }

}