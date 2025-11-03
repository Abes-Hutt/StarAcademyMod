package abeshutt.staracademy.math.random;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.math.random.lcg.Lcg;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class LcgRandom implements RandomSource {

	protected Lcg lcg;
	protected long seed;

	protected LcgRandom(Lcg lcg, long seed) {
		this.lcg = lcg;
		this.seed = seed;
	}

	public static LcgRandom of(Lcg lcg, long seed) {
		return new LcgRandom(lcg, seed);
	}

	public Lcg getLCG() {
		return this.lcg;
	}

	public long getSeed() {
		return this.seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public long nextSeed() {
		return this.seed = this.lcg.nextSeed(this.seed);
	}

	@Override
	public long nextLong() {
		return this.nextSeed();
	}

	@Override
	public void writeBits(BitBuffer buffer) {
		Adapters.LCG.writeBits(this.lcg, buffer);

		if(this.lcg.modulus == 0) {
			Adapters.LONG.writeBits(this.seed, buffer);
		} else {
			Adapters.ofBoundedLong(this.lcg.modulus).writeBits(this.seed, buffer);
		}
	}

	@Override
	public void readBits(BitBuffer buffer) {
		this.lcg = Adapters.LCG.readBits(buffer).orElseThrow();

		if(this.lcg.modulus == 0) {
			this.seed = Adapters.LONG.readBits(buffer).orElseThrow();
		} else {
			this.seed = Adapters.ofBoundedLong(this.lcg.modulus).readBits(buffer).orElseThrow();
		}
	}

	@Override
	public Optional<NbtCompound> writeNbt() {
		NbtCompound nbt = new NbtCompound();
		Adapters.LCG.writeNbt(this.lcg).ifPresent(value -> nbt.put("lcg", value));
		Adapters.LONG.writeNbt(this.seed).ifPresent(value -> nbt.put("seed", value));
		return Optional.of(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		this.lcg = Adapters.LCG.readNbt((NbtCompound)nbt.get("lcg")).orElseThrow();
		this.seed = Adapters.LONG.readNbt(nbt.get("seed")).orElseThrow();
	}

	@Override
	public Optional<JsonObject> writeJson() {
		JsonObject json = new JsonObject();
		Adapters.LCG.writeJson(this.lcg).ifPresent(value -> json.add("lcg", value));
		Adapters.LONG.writeJson(this.seed).ifPresent(value -> json.add("seed", value));
		return Optional.of(json);
	}

	@Override
	public void readJson(JsonObject json) {
		this.lcg = Adapters.LCG.readJson((JsonObject)json.get("lcg")).orElseThrow();
		this.seed = Adapters.LONG.readJson(json.get("seed")).orElseThrow();
	}

}
