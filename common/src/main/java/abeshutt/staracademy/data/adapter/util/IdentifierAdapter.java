package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

import static net.minecraft.util.Identifier.DEFAULT_NAMESPACE;

public class IdentifierAdapter implements ISimpleAdapter<Identifier, NbtElement, JsonElement> {

    private final boolean nullable;

    public IdentifierAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public IdentifierAdapter asNullable() {
        return new IdentifierAdapter(true);
    }

    @Override
    public void writeBits(Identifier value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            buffer.writeIdentifier(value);
        }
    }

    @Override
    public Optional<Identifier> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(buffer.readIdentifier());
    }

    @Override
    public void writeBytes(Identifier value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            String string = value.getNamespace().equals(DEFAULT_NAMESPACE) ? value.getPath() : value.toString();
            Adapters.UTF_8.writeBytes(string, buffer);
        }
    }

    @Override
    public Optional<Identifier> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(Identifier.of(Adapters.UTF_8.readBytes(buffer).orElseThrow()));
    }

    @Override
    public void writeData(Identifier value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            String string = value.getNamespace().equals(DEFAULT_NAMESPACE) ? value.getPath() : value.toString();
            Adapters.UTF_8.writeData(string, data);
        }
    }

    @Override
    public Optional<Identifier> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(Identifier.of(Adapters.UTF_8.readData(data).orElseThrow()));
    }

    @Override
    public Optional<NbtElement> writeNbt(Identifier value) {
        if(value == null) {
            return Optional.empty();
        }

        String string = value.getNamespace().equals(DEFAULT_NAMESPACE) ? value.getPath() : value.toString();
        return Optional.of(NbtString.of(string));
    }

    @Override
    public Optional<Identifier> readNbt(NbtElement nbt) {
        if(nbt instanceof NbtString string) {
            return Optional.ofNullable(Identifier.tryParse(string.asString()));
        } else if(nbt instanceof AbstractNbtList<?> array && array.size() == 1) {
            return this.readNbt(array.get(0));
        } else if(nbt instanceof AbstractNbtList<?> array && array.size() == 2) {
            String namespace = Adapters.UTF_8.readNbt(array.get(0)).orElse(null);
            String path = Adapters.UTF_8.readNbt(array.get(1)).orElse(null);

            if(namespace == null || path == null) {
                return Optional.empty();
            }

            return Optional.ofNullable(Identifier.tryParse(namespace + ":" + path));
        }

        return Optional.empty();
    }

    @Override
    public Optional<JsonElement> writeJson(Identifier value) {
        if(value == null) {
            return Optional.empty();
        }

        String string = value.getNamespace().equals(DEFAULT_NAMESPACE) ? value.getPath() : value.toString();
        return Optional.of(new JsonPrimitive(string));
    }

    @Override
    public Optional<Identifier> readJson(JsonElement json) {
        if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            return Optional.ofNullable(Identifier.tryParse(primitive.getAsString()));
        } else if(json instanceof JsonArray array && array.size() == 1) {
            return this.readJson(array.get(0));
        } else if(json instanceof JsonArray array && array.size() == 2) {
            String namespace = Adapters.UTF_8.readJson(array.get(0)).orElse(null);
            String path = Adapters.UTF_8.readJson(array.get(1)).orElse(null);

            if(namespace == null || path == null) {
                return Optional.empty();
            }

            return Optional.ofNullable(Identifier.tryParse(namespace + ":" + path));
        }

        return Optional.empty();
    }

}
