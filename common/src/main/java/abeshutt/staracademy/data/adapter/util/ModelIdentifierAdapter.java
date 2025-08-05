package abeshutt.staracademy.data.adapter.util;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class ModelIdentifierAdapter implements ISimpleAdapter<ModelIdentifier, NbtElement, JsonElement> {

    private final boolean nullable;

    public ModelIdentifierAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public ModelIdentifierAdapter asNullable() {
        return new ModelIdentifierAdapter(true);
    }

    @Override
    public void writeBits(ModelIdentifier value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.IDENTIFIER.writeBits(value.id(), buffer);
            Adapters.UTF_8.writeBits(value.getVariant(), buffer);
        }
    }

    @Override
    public Optional<ModelIdentifier> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(new ModelIdentifier(
                Adapters.IDENTIFIER.readBits(buffer).orElseThrow(),
                Adapters.UTF_8.readBits(buffer).orElseThrow()
        ));
    }

    @Override
    public void writeBytes(ModelIdentifier value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.IDENTIFIER.writeBytes(value.id(), buffer);
            Adapters.UTF_8.writeBytes(value.getVariant(), buffer);
        }
    }

    @Override
    public Optional<ModelIdentifier> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(new ModelIdentifier(
                Adapters.IDENTIFIER.readBytes(buffer).orElseThrow(),
                Adapters.UTF_8.readBytes(buffer).orElseThrow()
        ));
    }

    @Override
    public void writeData(ModelIdentifier value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            Adapters.IDENTIFIER.writeData(value.id(), data);
            Adapters.UTF_8.writeData(value.getVariant(), data);
        }
    }

    @Override
    public Optional<ModelIdentifier> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(new ModelIdentifier(
                Adapters.IDENTIFIER.readData(data).orElseThrow(),
                Adapters.UTF_8.readData(data).orElseThrow()
        ));
    }

    @Override
    public Optional<NbtElement> writeNbt(ModelIdentifier value) {
        if(value == null) {
            return Optional.empty();
        }

        return Adapters.UTF_8.writeNbt(value.toString());
    }

    @Override
    public Optional<ModelIdentifier> readNbt(NbtElement nbt) {
        if(nbt instanceof NbtString string) {
            String[] parts = string.asString().split(Pattern.quote("#"));

            if(parts.length == 2) {
                Identifier id = Identifier.tryParse(parts[0]);

                if(id != null) {
                    return Optional.of(new ModelIdentifier(id, parts[1]));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<JsonElement> writeJson(ModelIdentifier value) {
        if(value == null) {
            return Optional.empty();
        }

        return Adapters.UTF_8.writeJson(value.toString());
    }

    @Override
    public Optional<ModelIdentifier> readJson(JsonElement json) {
        if(json instanceof JsonPrimitive primitive && primitive.isString()) {
            String[] parts = primitive.getAsString().split(Pattern.quote("#"));

            if(parts.length == 2) {
                Identifier id = Identifier.tryParse(parts[0]);

                if(id != null) {
                    return Optional.of(new ModelIdentifier(id, parts[1]));
                }
            }
        }

        return Optional.empty();
    }

}
