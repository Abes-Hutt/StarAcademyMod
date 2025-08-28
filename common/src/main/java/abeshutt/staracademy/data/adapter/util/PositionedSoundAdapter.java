package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.CheckedRandom;

import java.util.Optional;
import java.util.Random;

import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.NAME;

public class PositionedSoundAdapter implements ISimpleAdapter<PositionedSoundInstance, NbtElement, JsonElement> {

	private final boolean nullable;

	public PositionedSoundAdapter(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public PositionedSoundAdapter asNullable() {
		return new PositionedSoundAdapter(true);
	}

	@Override
	public void writeBits(PositionedSoundInstance value, BitBuffer buffer) {
		if(this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if(value != null) {
			Adapters.IDENTIFIER.writeBits(value.getId(), buffer);
			Adapters.ofEnum(SoundCategory.class, NAME).writeBits(value.getCategory(), buffer);
			Adapters.FLOAT.writeBits(value.getVolume(), buffer);
			Adapters.FLOAT.writeBits(value.getPitch(), buffer);
			Adapters.BOOLEAN.writeBits(value.isRepeatable(), buffer);
			Adapters.INT.writeBits(value.getRepeatDelay(), buffer);
			Adapters.ofEnum(SoundInstance.AttenuationType.class, NAME).writeBits(value.getAttenuationType(), buffer);
			Adapters.DOUBLE.writeBits(value.getX(), buffer);
			Adapters.DOUBLE.writeBits(value.getY(), buffer);
			Adapters.DOUBLE.writeBits(value.getZ(), buffer);
			Adapters.BOOLEAN.writeBits(value.isRelative(), buffer);
		}
	}

	@Override
	public Optional<PositionedSoundInstance> readBits(BitBuffer buffer) {
		if(this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new PositionedSoundInstance(
				Adapters.IDENTIFIER.readBits(buffer).orElseThrow(),
				Adapters.ofEnum(SoundCategory.class, NAME).readBits(buffer).orElseThrow(),
				Adapters.FLOAT.readBits(buffer).orElseThrow(),
				Adapters.FLOAT.readBits(buffer).orElseThrow(),
				new CheckedRandom(new Random().nextLong()),
				Adapters.BOOLEAN.readBits(buffer).orElseThrow(),
				Adapters.INT.readBits(buffer).orElseThrow(),
				Adapters.ofEnum(SoundInstance.AttenuationType.class, NAME).readBits(buffer).orElseThrow(),
				Adapters.DOUBLE.readBits(buffer).orElseThrow(),
				Adapters.DOUBLE.readBits(buffer).orElseThrow(),
				Adapters.DOUBLE.readBits(buffer).orElseThrow(),
				Adapters.BOOLEAN.readBits(buffer).orElseThrow()
		));
	}

	@Override
	public Optional<JsonElement> writeJson(PositionedSoundInstance value) {
        if(value == null) {
			return Optional.empty();
		}

		JsonObject json = new JsonObject();
		Adapters.IDENTIFIER.writeJson(value.getId()).ifPresent(tag -> json.add("id", tag));
		Adapters.ofEnum(SoundCategory.class, NAME).writeJson(value.getCategory()).ifPresent(tag -> json.add("category", tag));
		Adapters.FLOAT.writeJson(value.getVolume()).ifPresent(tag -> json.add("volume", tag));
		Adapters.FLOAT.writeJson(value.getPitch()).ifPresent(tag -> json.add("pitch", tag));
		Adapters.BOOLEAN.writeJson(value.isRepeatable()).ifPresent(tag -> json.add("repeat", tag));
		Adapters.INT.writeJson(value.getRepeatDelay()).ifPresent(tag -> json.add("repeatDelay", tag));
		Adapters.ofEnum(SoundInstance.AttenuationType.class, NAME).writeJson(value.getAttenuationType()).ifPresent(tag -> json.add("attenuation", tag));
		Adapters.VEC_3D.writeJson(new Vec3d(value.getX(), value.getY(), value.getZ())).ifPresent(tag -> json.add("position", tag));
		Adapters.BOOLEAN.writeJson(value.isRelative()).ifPresent(tag -> json.add("relative", tag));
		return Optional.of(json);
    }

	@Override
	public Optional<PositionedSoundInstance> readJson(JsonElement json) {
		if(!(json instanceof JsonObject object)) {
			return Optional.empty();
		}

		return Optional.of(new PositionedSoundInstance(
				Adapters.IDENTIFIER.readJson(object.get("id")).orElseThrow(),
				Adapters.ofEnum(SoundCategory.class, NAME).readJson(object.get("category")).orElseThrow(),
				Adapters.FLOAT.readJson(object.get("volume")).orElseThrow(),
				Adapters.FLOAT.readJson(object.get("pitch")).orElseThrow(),
				new CheckedRandom(new Random().nextLong()),
				Adapters.BOOLEAN.readJson(object.get("repeat")).orElseThrow(),
				Adapters.INT.readJson(object.get("repeatDelay")).orElseThrow(),
				Adapters.ofEnum(SoundInstance.AttenuationType.class, NAME).readJson(object.get("attenuation")).orElseThrow(),
				Adapters.DOUBLE.readJson(object.get("x")).orElseThrow(),
				Adapters.DOUBLE.readJson(object.get("y")).orElseThrow(),
				Adapters.DOUBLE.readJson(object.get("z")).orElseThrow(),
				Adapters.BOOLEAN.readJson(object.get("relative")).orElseThrow()
		));
	}

}
