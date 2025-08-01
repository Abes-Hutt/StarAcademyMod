package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Base64;
import java.util.Optional;

public class DynamicTexture {

    private final Identifier id;
    private final int width;
    private final int height;

    public DynamicTexture(Identifier id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public static class Adapter implements ISimpleAdapter<DynamicTexture, NbtElement, JsonElement> {
        @Override
        public Optional<JsonElement> writeJson(DynamicTexture value) {
            return Optional.of(new JsonObject()).map(json -> {
                Adapters.IDENTIFIER.writeJson(value.id).ifPresent(tag -> json.add("id", tag));
                Adapters.INT.writeJson(value.width).ifPresent(tag -> json.add("width", tag));
                Adapters.INT.writeJson(value.height).ifPresent(tag -> json.add("height", tag));
                return json;
            });
        }

        @Override
        public Optional<DynamicTexture> readJson(JsonElement json) {
            if(!(json instanceof JsonObject object)) {
                return Optional.empty();
            }

            return Optional.of(new DynamicTexture(
                Adapters.IDENTIFIER.readJson(object.get("id")).orElseThrow(),
                Adapters.INT.readJson(object.get("width")).orElseThrow(),
                Adapters.INT.readJson(object.get("height")).orElseThrow()
            ));
        }
    }

}
