package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.nbt.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class Vec2fAdapter implements ISimpleAdapter<Vector2f, NbtElement, JsonElement> {

	private final boolean nullable;

	public Vec2fAdapter(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public Vec2fAdapter asNullable() {
		return new Vec2fAdapter(true);
	}

	@Override
	public void writeBits(Vector2f value, BitBuffer buffer) {
		if(this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if(value != null) {
			Adapters.FLOAT.writeBits(value.getX(), buffer);
			Adapters.FLOAT.writeBits(value.getY(), buffer);
		}
	}

	@Override
	public Optional<Vector2f> readBits(BitBuffer buffer) {
		if(this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Vector2f(
			Adapters.FLOAT.readBits(buffer).orElseThrow(),
			Adapters.FLOAT.readBits(buffer).orElseThrow()
		));
	}

	@Override
	public void writeBytes(Vector2f value, ByteBuf buffer) {
		if(this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if(value != null) {
			Adapters.FLOAT.writeBytes(value.getX(), buffer);
			Adapters.FLOAT.writeBytes(value.getY(), buffer);
		}
	}

	@Override
	public Optional<Vector2f> readBytes(ByteBuf buffer) {
		if (this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Vector2f(
			Adapters.FLOAT.readBytes(buffer).orElseThrow(),
			Adapters.FLOAT.readBytes(buffer).orElseThrow()
		));
	}

	@Override
	public void writeData(Vector2f value, DataOutput data) throws IOException {
		if(this.nullable) {
			data.writeBoolean(value == null);
		}

		if(value != null) {
			Adapters.FLOAT.writeData(value.getX(), data);
			Adapters.FLOAT.writeData(value.getY(), data);
		}
	}

	@Override
	public Optional<Vector2f> readData(DataInput data) throws IOException {
		if(this.nullable && data.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Vector2f(
			Adapters.FLOAT.readData(data).orElseThrow(),
			Adapters.FLOAT.readData(data).orElseThrow()
		));
	}

	@Override
	public Optional<NbtElement> writeNbt(Vector2f value) {
		if(value == null) {
			return Optional.empty();
		}

		NbtList list = new NbtList();
		list.add(NbtFloat.of(value.getX()));
		list.add(NbtFloat.of(value.getY()));
		return Optional.of(list);
	}

	@Override
	public Optional<Vector2f> readNbt(NbtElement nbt) {
		if(nbt == null) {
			return Optional.empty();
		}

		if(nbt instanceof AbstractNbtList<?> list && list.size() == 3) {
			return Optional.of(new Vector2f(
				Adapters.FLOAT.readNbt(list.get(0)).orElseThrow(),
				Adapters.FLOAT.readNbt(list.get(1)).orElseThrow()
			));
		} else if(nbt instanceof NbtCompound compound) {
			return Optional.of(new Vector2f(
				Adapters.FLOAT.readNbt(compound.get("x")).orElseThrow(),
				Adapters.FLOAT.readNbt(compound.get("y")).orElseThrow()
			));
		}

        return Optional.empty();
    }

	@Override
	public Optional<JsonElement> writeJson(Vector2f value) {
        if(value == null) {
			return Optional.empty();
		}

		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(value.getX()));
		array.add(new JsonPrimitive(value.getY()));
		return Optional.of(array);
    }

	@Override
	public Optional<Vector2f> readJson(JsonElement json) {
		if(json instanceof JsonArray array && array.size() == 2) {
			return Optional.of(new Vector2f(
				Adapters.FLOAT.readJson(array.get(0)).orElseThrow(),
				Adapters.FLOAT.readJson(array.get(1)).orElseThrow()
			));
		} else if(json instanceof JsonObject object) {
			return Optional.of(new Vector2f(
				Adapters.FLOAT.readJson(object.get("x")).orElseThrow(),
				Adapters.FLOAT.readJson(object.get("y")).orElseThrow()
			));
		}

		return Optional.empty();
	}

}
