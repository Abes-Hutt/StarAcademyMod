package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;
import org.joml.Vector3f;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class Vec3fAdapter implements ISimpleAdapter<Vector3f, NbtElement, JsonElement> {

	private final boolean nullable;

	public Vec3fAdapter(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public Vec3fAdapter asNullable() {
		return new Vec3fAdapter(true);
	}

	@Override
	public void writeBits(Vector3f value, BitBuffer buffer) {
		if(this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if(value != null) {
			Adapters.FLOAT.writeBits(value.x, buffer);
			Adapters.FLOAT.writeBits(value.y, buffer);
			Adapters.FLOAT.writeBits(value.z, buffer);
		}
	}

	@Override
	public Optional<Vector3f> readBits(BitBuffer buffer) {
		if(this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Vector3f(
			Adapters.FLOAT.readBits(buffer).orElseThrow(),
			Adapters.FLOAT.readBits(buffer).orElseThrow(),
			Adapters.FLOAT.readBits(buffer).orElseThrow()
		));
	}

	@Override
	public void writeBytes(Vector3f value, ByteBuf buffer) {
		if (this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if (value != null) {
			Adapters.FLOAT.writeBytes(value.x, buffer);
			Adapters.FLOAT.writeBytes(value.y, buffer);
			Adapters.FLOAT.writeBytes(value.z, buffer);
		}
	}

	@Override
	public Optional<Vector3f> readBytes(ByteBuf buffer) {
		if (this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Vector3f(
			Adapters.FLOAT.readBytes(buffer).orElseThrow(),
			Adapters.FLOAT.readBytes(buffer).orElseThrow(),
			Adapters.FLOAT.readBytes(buffer).orElseThrow()
		));
	}

	@Override
	public void writeData(Vector3f value, DataOutput data) throws IOException {
		if (this.nullable) {
			data.writeBoolean(value == null);
		}

		if (value != null) {
			Adapters.FLOAT.writeData(value.x, data);
			Adapters.FLOAT.writeData(value.y, data);
			Adapters.FLOAT.writeData(value.z, data);
		}
	}

	@Override
	public Optional<Vector3f> readData(DataInput data) throws IOException {
		if (this.nullable && data.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Vector3f(
			Adapters.FLOAT.readData(data).orElseThrow(),
			Adapters.FLOAT.readData(data).orElseThrow(),
			Adapters.FLOAT.readData(data).orElseThrow()
		));
	}

	@Override
	public Optional<NbtElement> writeNbt(Vector3f value) {
		if(value == null) {
			return Optional.empty();
		}

		NbtList list = new NbtList();
		list.add(NbtFloat.of(value.x));
		list.add(NbtFloat.of(value.y));
		list.add(NbtFloat.of(value.z));
		return Optional.of(list);
	}

	@Override
	public Optional<Vector3f> readNbt(NbtElement nbt) {
		if(nbt == null) {
			return Optional.empty();
		}

		if(nbt instanceof AbstractNbtList<?> list && list.size() == 3) {
			return Optional.of(new Vector3f(
				Adapters.FLOAT.readNbt(list.get(0)).orElseThrow(),
				Adapters.FLOAT.readNbt(list.get(1)).orElseThrow(),
				Adapters.FLOAT.readNbt(list.get(2)).orElseThrow()
			));
		} else if(nbt instanceof NbtCompound compound) {
			return Optional.of(new Vector3f(
				Adapters.FLOAT.readNbt(compound.get("x")).orElseThrow(),
				Adapters.FLOAT.readNbt(compound.get("y")).orElseThrow(),
				Adapters.FLOAT.readNbt(compound.get("z")).orElseThrow()
			));
		}

        return Optional.empty();
    }

	@Override
	public Optional<JsonElement> writeJson(Vector3f value) {
        if(value == null) {
			return Optional.empty();
		}

		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(value.x));
		array.add(new JsonPrimitive(value.y));
		array.add(new JsonPrimitive(value.z));
		return Optional.of(array);
    }

	@Override
	public Optional<Vector3f> readJson(JsonElement json) {
		if(json instanceof JsonArray array && array.size() == 3) {
			return Optional.of(new Vector3f(
				Adapters.FLOAT.readJson(array.get(0)).orElseThrow(),
				Adapters.FLOAT.readJson(array.get(1)).orElseThrow(),
				Adapters.FLOAT.readJson(array.get(2)).orElseThrow()
			));
		} else if(json instanceof JsonObject object) {
			return Optional.of(new Vector3f(
				Adapters.FLOAT.readJson(object.get("x")).orElseThrow(),
				Adapters.FLOAT.readJson(object.get("y")).orElseThrow(),
				Adapters.FLOAT.readJson(object.get("z")).orElseThrow()
			));
		}

		return Optional.empty();
	}

}
