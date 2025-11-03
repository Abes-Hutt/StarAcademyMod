package abeshutt.staracademy.math.random;

public abstract class XoroshiroRandom implements RandomSource {

    public abstract void setSeed(long seed);

    public long nextBits(int bits) {
        return this.nextLong() >>> 64 - bits;
    }

    @Override
    public boolean nextBoolean() {
        return (this.nextLong() & 1L) != 0L;
    }

    @Override
    public int nextInt() {
        return (int)this.nextLong();
    }

    @Override
    public int nextInt(int bound) {
        if(bound <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }

        long i = (long)this.nextInt() & ((1L << 32) - 1);
        long j = i * (long)bound;
        long k = j & ((1L << 32) - 1);

        if(k < (long)bound) {
            for(int l = Integer.remainderUnsigned(~bound + 1, bound); k < (long)l; k = j & ((1L << 32) - 1)) {
                i = (long)this.nextInt() & ((1L << 32) - 1);
                j = i * (long)bound;
            }
        }

        return (int)(j >> 32);
    }

    @Override
    public float nextFloat() {
        return (float)this.nextBits(24) * 5.9604645E-8F;
    }

    @Override
    public double nextDouble() {
        return (double)this.nextBits(53) * (double)1.110223E-16F;
    }

}