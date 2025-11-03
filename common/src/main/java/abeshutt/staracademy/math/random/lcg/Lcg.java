package abeshutt.staracademy.math.random.lcg;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.math.MathUtils;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;
import java.util.Optional;

public class Lcg {

    public static final Lcg CC65_M23 = new Lcg(65793L, 4282663L, 1L << 23);

    public static final Lcg VISUAL_BASIC = new Lcg(1140671485L, 12820163L, 1L << 24);

    public static final Lcg RTL_UNIFORM = new Lcg(2147483629L, 2147483587L, (1L << 31) - 1);
    public static final Lcg MINSTD_RAND0_C = new Lcg(16807L, 0L, (1L << 31) - 1);
    public static final Lcg MINSTD_RAND_C = new Lcg(48271, 0L, (1L << 31) - 1);

    public static final Lcg CC65_M31 = new Lcg(16843009L, 826366247L, 1L << 23);
    public static final Lcg RANDU = new Lcg(65539L, 0L, 1L << 31);
    public static final Lcg GLIB_C = new Lcg(1103515245L, 12345L, 1L << 31);

    public static final Lcg BORLAND_C = new Lcg(22695477L, 1L, 1L << 32);
    public static final Lcg PASCAL = new Lcg(134775813L, 1L, 1L << 32);
    public static final Lcg OPEN_VMS = new Lcg(69069L, 1L, 1L << 32);
    public static final Lcg NUMERICAL_RECIPES = new Lcg(1664525L, 1013904223L, 1L << 32);
    public static final Lcg MS_VISUAL_C = new Lcg(214013L, 2531011L, 1L << 32);

    public static final Lcg JAVA = new Lcg(25214903917L, 11L, 1L << 48);

    public static final Lcg JAVA_UNIQUIFIER_OLD = new Lcg(181783497276652981L, 0L);
    public static final Lcg JAVA_UNIQUIFIER_NEW = new Lcg(1181783497276652981L, 0L);
    public static final Lcg MMIX = new Lcg(6364136223846793005L, 1442695040888963407L);
    public static final Lcg NEWLIB_C = new Lcg(6364136223846793005L, 1L);
    public static final Lcg XKCD = new Lcg(0L, 4L);

    public final long multiplier;
    public final long addend;
    public final long modulus;

    private final boolean isPowerOf2;
    private final int trailingZeros;

    public Lcg(long multiplier, long addend) { //Modulus is 2^64.
        this(multiplier, addend, 0);
    }

    public Lcg(long multiplier, long addend, long modulus) {
        this.multiplier = multiplier;
        this.addend = addend;
        this.modulus = modulus;

        this.isPowerOf2 = this.modulus == 0 || MathUtils.isPowerOf2(this.modulus);
        this.trailingZeros = this.isPowerOf2 ? Long.numberOfTrailingZeros(this.modulus) : -1;
    }

    public static Lcg combine(Lcg... lcgs) {
        Lcg lcg = lcgs[0];

        for(int i = 1; i < lcgs.length; i++) {
            lcg = lcg.combine(lcgs[i]);
        }

        return lcg;
    }

    public boolean isModPowerOf2() {
        return this.isPowerOf2;
    }

    public int getModTrailingZeroes() {
        return this.trailingZeros;
    }

    public boolean isMultiplicative() {
        return this.addend == 0;
    }

    public long nextSeed(long seed) {
        return this.mod(seed * this.multiplier + this.addend);
    }

    public long mod(long n) {
        if (this.isModPowerOf2()) {
            return n & (this.modulus - 1);
        } else if (n <= 1L << 32) {
            return Long.remainderUnsigned(n, this.modulus);
        }

        throw new UnsupportedOperationException();
    }

    public Lcg combine(long steps) {
        long multiplier = 1;
        long addend = 0;

        long intermediateMultiplier = this.multiplier;
        long intermediateAddend = this.addend;

        for (long k = steps; k != 0; k >>>= 1) {
            if ((k & 1) != 0) {
                multiplier *= intermediateMultiplier;
                addend = intermediateMultiplier * addend + intermediateAddend;
            }

            intermediateAddend = (intermediateMultiplier + 1) * intermediateAddend;
            intermediateMultiplier *= intermediateMultiplier;
        }

        multiplier = this.mod(multiplier);
        addend = this.mod(addend);

        return new Lcg(multiplier, addend, this.modulus);
    }

    public Lcg combine(Lcg lcg) {
        if(this.modulus != lcg.modulus) {
            throw new UnsupportedOperationException();
        }

        return new Lcg(this.multiplier * lcg.multiplier, lcg.multiplier * this.addend + lcg.addend, this.modulus);
    }

    public Lcg invert() {
        return this.combine(-1);
    }

    public long distance(long seed1, long seed2) {
        if(DiscreteLog.supports(this)) {
            long aFromZero = DiscreteLog.distanceFromZero(this, seed1);
            long bFromZero = DiscreteLog.distanceFromZero(this, seed2);
            return MathUtils.maskSigned(bFromZero - aFromZero, this.getModTrailingZeroes());
        }

        throw new UnsupportedOperationException("DiscreteLog is not supported by this LCG");
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(!(other instanceof Lcg lcg)) return false;
        return this.multiplier == lcg.multiplier && this.addend == lcg.addend && this.modulus == lcg.modulus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.multiplier, this.addend, this.modulus);
    }

    @Override
    public String toString() {
        return "LCG{" + "multiplier=" + this.multiplier +
            ", addend=" + this.addend + ", modulus=" + this.modulus + '}';
    }

    public static class Adapter implements ISimpleAdapter<Lcg, NbtCompound, JsonObject> {
        private boolean nullable;

        public Adapter(boolean nullable) {
            this.nullable = nullable;
        }

        public Adapter asNullable() {
            return new Adapter(true);
        }

        @Override
        public void writeBits(Lcg lcg, BitBuffer buffer) {
            if(this.nullable) {
                buffer.writeBoolean(lcg == null);
            }

            if(lcg != null) {
                Adapters.LONG.writeBits(lcg.multiplier, buffer);
                Adapters.LONG_SEGMENTED_15.writeBits(lcg.addend, buffer);
                Adapters.LONG.writeBits(lcg.modulus, buffer);
            }
        }

        @Override
        public Optional<Lcg> readBits(BitBuffer buffer) {
            if(this.nullable && buffer.readBoolean()) {
                return Optional.empty();
            }

            return Optional.of(new Lcg(
            Adapters.LONG.readBits(buffer).orElseThrow(),
            Adapters.LONG_SEGMENTED_15.readBits(buffer).orElseThrow(),
            Adapters.LONG.readBits(buffer).orElseThrow()
            ));
        }
    }

}
