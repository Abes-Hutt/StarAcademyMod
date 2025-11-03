package abeshutt.staracademy.math.random;

public class Xoroshiro128PlusPlusRandom extends XoroshiroRandom {

    protected long upperBits;
    protected long lowerBits;

    public Xoroshiro128PlusPlusRandom(long upperBits, long lowerBits) {
        this.upperBits = upperBits;
        this.lowerBits = lowerBits;

        if((this.lowerBits | this.upperBits) == 0L) {
            this.upperBits = 7640891576956012809L;
            this.lowerBits = -7046029254386353131L;
        }
    }

    @Override
    public void setSeed(long seed) {
        this.lowerBits = seed ^ 7640891576956012809L;
        this.upperBits = this.lowerBits - 7046029254386353131L;

        this.lowerBits = (this.lowerBits ^ this.lowerBits >>> 30) * -4658895280553007687L;
        this.lowerBits = (this.lowerBits ^ this.lowerBits >>> 27) * -7723592293110705685L;
        this.lowerBits = this.lowerBits ^ this.lowerBits >>> 31;

        this.upperBits = (this.upperBits ^ this.upperBits >>> 30) * -4658895280553007687L;
        this.upperBits = (this.upperBits ^ this.upperBits >>> 27) * -7723592293110705685L;
        this.upperBits = this.upperBits ^ this.upperBits >>> 31;
    }

    @Override
    public long nextLong() {
        long xor = this.upperBits ^ this.lowerBits;
        long value = Long.rotateLeft(this.lowerBits + this.upperBits, 17) + this.lowerBits;
        this.lowerBits = Long.rotateLeft(this.lowerBits, 49) ^ xor ^ xor << 21;
        this.upperBits = Long.rotateLeft(xor, 28);
        return value;
    }

}
