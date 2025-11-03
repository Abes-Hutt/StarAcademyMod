package abeshutt.staracademy.math.random.lcg;

import abeshutt.staracademy.math.MathUtils;

import java.math.BigInteger;

public class DiscreteLog {

    public static boolean supports(Lcg lcg) {
        if(!lcg.isModPowerOf2() || lcg.getModTrailingZeroes() > 61) return false;
        return lcg.multiplier % 2 != 0 && lcg.addend % 2 != 0;
    }

    public static long distanceFromZero(Lcg lcg, long seed) {
        int exp = lcg.getModTrailingZeroes();

        long a = lcg.multiplier;
        long b = MathUtils.mask(seed * (lcg.multiplier - 1) * MathUtils.modInverse(lcg.addend, exp) + 1, exp + 2);
        long aBar = theta(a, exp);
        long bBar = theta(b, exp);
        return bBar * MathUtils.mask(MathUtils.modInverse(aBar, exp), exp);
    }

    private static long theta(long number, int exp) {
        if(number % 4 == 3) {
            number = MathUtils.getPow2(exp + 2) - number;
        }

        BigInteger xHat = BigInteger.valueOf(number);
        xHat = xHat.modPow(BigInteger.ONE.shiftLeft(exp + 1), BigInteger.ONE.shiftLeft(2 * exp + 3));
        xHat = xHat.subtract(BigInteger.ONE);
        xHat = xHat.divide(BigInteger.ONE.shiftLeft(exp + 3));
        xHat = xHat.mod(BigInteger.ONE.shiftLeft(exp));
        return xHat.longValue();
    }

}
