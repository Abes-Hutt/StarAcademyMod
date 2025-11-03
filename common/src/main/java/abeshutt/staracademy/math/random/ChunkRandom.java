package abeshutt.staracademy.math.random;

import abeshutt.staracademy.math.MathUtils;
import net.minecraft.util.math.BlockPos;

public class ChunkRandom extends JavaRandom {

	protected ChunkRandom(long seed) {
		super(seed);
	}

	public static ChunkRandom any() {
		return new ChunkRandom(0L);
	}

	public static ChunkRandom ofInternal(long seed) {
		return new ChunkRandom(seed);
	}

	public static ChunkRandom ofScrambled(long seed) {
		return new ChunkRandom(seed ^ MULTIPLIER);
	}

	public static ChunkRandom wrap(LcgRandom random) {
		return new Wrapper(random);
	}

	public long setTerrainSeed(int chunkX, int chunkZ) {
		long seed = (long)chunkX * 341873128712L + (long)chunkZ * 132897987541L;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setPopulationSeed(long worldSeed, int x, int z) {
		this.setSeed(worldSeed);
		long a = this.nextLong() | 1L;
		long b = this.nextLong() | 1L;
		long seed = (long)x * a + (long)z * b ^ worldSeed;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setDecoratorSeed(long populationSeed, int index, int step) {
		return this.setDecoratorSeed(populationSeed, index + 10000 * step);
	}

	public long setDecoratorSeed(long populationSeed, int salt) {
		long seed = populationSeed + salt;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setDecoratorSeed(long worldSeed, int blockX, int blockZ, int index, int step) {
		long populationSeed = this.setPopulationSeed(worldSeed, blockX, blockZ);
		return this.setDecoratorSeed(populationSeed, index, step);
	}

	public long setDecoratorSeed(long worldSeed, int blockX, int blockZ, int salt) {
		long populationSeed = this.setPopulationSeed(worldSeed, blockX, blockZ);
		return this.setDecoratorSeed(populationSeed, salt);
	}

	public long setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
		this.setSeed(worldSeed);
		long a = this.nextLong();
		long b = this.nextLong();
		long seed = (long)chunkX * a ^ (long)chunkZ * b ^ worldSeed;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setRegionSeed(long worldSeed, int regionX, int regionZ, long salt) {
		long seed = (long)regionX * 341873128712L + (long)regionZ * 132897987541L + worldSeed + salt;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setWeakSeed(long worldSeed, int chunkX, int chunkZ) {
		int sX = chunkX >> 4;
		int sZ = chunkZ >> 4;
		long seed = (long)(sX ^ sZ << 4) ^ worldSeed;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setSlimeSeed(long worldSeed, int chunkX, int chunkZ, long scrambler) {
		long seed = worldSeed + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611)
			            + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ scrambler;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setSlimeSeed(long worldSeed, int chunkX, int chunkZ) {
		return this.setSlimeSeed(worldSeed, chunkX, chunkZ, 987234911L);
	}

	public long setModelSeed(int blockX, int blockY, int blockZ) {
		long seed = (long)(blockX * 3129871) ^ (long)blockZ * 116129781L ^ (long)blockY;
		seed = (seed * seed * 42317861L + seed * 11L) >> 16;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	public long setBlockSeed(long worldSeed, BlockPos pos, long salt) {
		return this.setBlockSeed(worldSeed, pos.getX(), pos.getY(), pos.getZ(), salt);
	}

	public long setBlockSeed(long worldSeed, int blockX, int blockY, int blockZ, long salt) {
		this.setSeed(worldSeed + salt);
		long a = this.nextLong() | 1L;
		long b = this.nextLong() | 1L;
		long c = this.nextLong() | 1L;
		long d = this.nextLong() | 1L;
		long seed = (long)blockX * a + (long)blockY * b + (long)blockZ * c + salt * d ^ worldSeed;
		this.setSeed(seed);
		return seed & MathUtils.MASK_48;
	}

	protected static class Wrapper extends ChunkRandom {
		private final LcgRandom delegate;

		protected Wrapper(LcgRandom delegate) {
			super(delegate.getSeed());
			this.delegate = delegate;
		}

		@Override
		public void setSeed(long seed) {
			this.delegate.setSeed(seed);
		}

		@Override
		public long nextLong() {
			return this.delegate.nextLong();
		}
	}

}
