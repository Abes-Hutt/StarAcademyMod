package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.model.Dilation;
import net.minecraft.nbt.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class DilationAdapter implements ISimpleAdapter<Dilation, NbtElement, JsonElement> {

	private final boolean nullable;

	public DilationAdapter(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public DilationAdapter asNullable() {
		return new DilationAdapter(true);
	}

	@Override
	public void writeBits(Dilation value, BitBuffer buffer) {
		if(this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if(value != null) {
			Adapters.FLOAT.writeBits(value.radiusX, buffer);
			Adapters.FLOAT.writeBits(value.radiusY, buffer);
			Adapters.FLOAT.writeBits(value.radiusZ, buffer);
		}
	}

	@Override
	public Optional<Dilation> readBits(BitBuffer buffer) {
		if(this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Dilation(
			Adapters.FLOAT.readBits(buffer).orElseThrow(),
			Adapters.FLOAT.readBits(buffer).orElseThrow(),
			Adapters.FLOAT.readBits(buffer).orElseThrow()
		));
	}

	@Override
	public void writeBytes(Dilation value, ByteBuf buffer) {
		if (this.nullable) {
			buffer.writeBoolean(value == null);
		}

		if (value != null) {
			Adapters.FLOAT.writeBytes(value.radiusX, buffer);
			Adapters.FLOAT.writeBytes(value.radiusY, buffer);
			Adapters.FLOAT.writeBytes(value.radiusZ, buffer);
		}
	}

	@Override
	public Optional<Dilation> readBytes(ByteBuf buffer) {
		if (this.nullable && buffer.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Dilation(
			Adapters.FLOAT.readBytes(buffer).orElseThrow(),
			Adapters.FLOAT.readBytes(buffer).orElseThrow(),
			Adapters.FLOAT.readBytes(buffer).orElseThrow()
		));
	}

	@Override
	public void writeData(Dilation value, DataOutput data) throws IOException {
		if (this.nullable) {
			data.writeBoolean(value == null);
		}

		if (value != null) {
			Adapters.FLOAT.writeData(value.radiusX, data);
			Adapters.FLOAT.writeData(value.radiusY, data);
			Adapters.FLOAT.writeData(value.radiusZ, data);
		}
	}

	@Override
	public Optional<Dilation> readData(DataInput data) throws IOException {
		if (this.nullable && data.readBoolean()) {
			return Optional.empty();
		}

		return Optional.of(new Dilation(
			Adapters.FLOAT.readData(data).orElseThrow(),
			Adapters.FLOAT.readData(data).orElseThrow(),
			Adapters.FLOAT.readData(data).orElseThrow()
		));
	}

	@Override
	public Optional<NbtElement> writeNbt(Dilation value) {
		if(value == null) {
			return Optional.empty();
		}

		NbtList list = new NbtList();
		list.add(NbtFloat.of(value.radiusX));
		list.add(NbtFloat.of(value.radiusY));
		list.add(NbtFloat.of(value.radiusZ));
		return Optional.of(list);
	}

	@Override
	public Optional<Dilation> readNbt(NbtElement nbt) {
		if(nbt == null) {
			return Optional.empty();
		}

		if(nbt instanceof AbstractNbtList<?> list && list.size() == 3) {
			return Optional.of(new Dilation(
				Adapters.FLOAT.readNbt(list.get(0)).orElseThrow(),
				Adapters.FLOAT.readNbt(list.get(1)).orElseThrow(),
				Adapters.FLOAT.readNbt(list.get(2)).orElseThrow()
			));
		} else if(nbt instanceof NbtCompound compound) {
			return Optional.of(new Dilation(
				Adapters.FLOAT.readNbt(compound.get("x")).orElseThrow(),
				Adapters.FLOAT.readNbt(compound.get("y")).orElseThrow(),
				Adapters.FLOAT.readNbt(compound.get("z")).orElseThrow()
			));
		}

        return Optional.empty();
    }

	@Override
	public Optional<JsonElement> writeJson(Dilation value) {
        if(value == null) {
			return Optional.empty();
		}

		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(value.radiusX));
		array.add(new JsonPrimitive(value.radiusY));
		array.add(new JsonPrimitive(value.radiusZ));
		return Optional.of(array);
    }

	@Override
	public Optional<Dilation> readJson(JsonElement json) {
		if(json instanceof JsonArray array && array.size() == 3) {
			return Optional.of(new Dilation(
				Adapters.FLOAT.readJson(array.get(0)).orElseThrow(),
				Adapters.FLOAT.readJson(array.get(1)).orElseThrow(),
				Adapters.FLOAT.readJson(array.get(2)).orElseThrow()
			));
		} else if(json instanceof JsonObject object) {
			return Optional.of(new Dilation(
				Adapters.FLOAT.readJson(object.get("x")).orElseThrow(),
				Adapters.FLOAT.readJson(object.get("y")).orElseThrow(),
				Adapters.FLOAT.readJson(object.get("z")).orElseThrow()
			));
		}

		return Optional.empty();
	}

}
