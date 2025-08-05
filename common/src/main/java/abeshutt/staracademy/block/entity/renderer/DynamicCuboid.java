package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.adapter.array.ArrayAdapter;
import abeshutt.staracademy.data.adapter.basic.EnumAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class DynamicCuboid {

    private static final ArrayAdapter<Direction> DIRECTIONS = Adapters.ofArray(Direction[]::new,
            Adapters.ofEnum(Direction.class, EnumAdapter.Mode.NAME));

    public final Vector3f offset;
    public final Vector3f size;
    public final Dilation dilation;
    public final boolean mirror;
    public final Vector2f textureUV;
    public final Vector2f textureScale;
    public final Direction[] textureFaces;

    public DynamicCuboid(Vector3f offset, Vector3f size, Dilation dilation, boolean mirror, Vector2f textureUV,
                         Vector2f textureScale, Direction[] textureFaces) {
        this.offset = offset;
        this.size = size;
        this.dilation = dilation;
        this.mirror = mirror;
        this.textureUV = textureUV;
        this.textureScale = textureScale;
        this.textureFaces = textureFaces;
    }

    public ModelCuboidData build() {
        return new ModelCuboidData(null, this.textureUV.getX(), this.textureUV.getY(),
                this.offset.x, this.offset.y, this.offset.z,
                this.size.x, this.size.y, this.size.z, this.dilation, this.mirror,
                this.textureScale.getX(), this.textureScale.getY(),
                Set.of(this.textureFaces));
    }

    public static class Adapter implements ISimpleAdapter<DynamicCuboid, NbtElement, JsonElement> {
        @Override
        public Optional<JsonElement> writeJson(DynamicCuboid value) {
            return Optional.of(new JsonObject()).map(json -> {
                Adapters.VEC_3F.writeJson(value.offset).ifPresent(tag -> json.add("offset", tag));
                Adapters.VEC_3F.writeJson(value.size).ifPresent(tag -> json.add("size", tag));

                if(value.dilation.radiusX != 0.0F || value.dilation.radiusY != 0.0F || value.dilation.radiusZ != 0.0F) {
                    Adapters.DILATION.writeJson(value.dilation).ifPresent(tag -> json.add("dilation", tag));
                }

                if(value.mirror) {
                    Adapters.BOOLEAN.writeJson(true).ifPresent(tag -> json.add("mirror", tag));
                }

                JsonObject texture = new JsonObject();
                Adapters.VEC_2F.writeJson(value.textureUV).ifPresent(tag -> texture.add("uv", tag));

                if(value.textureScale.equals(new Vector2f(1.0F, 1.0F))) {
                    Adapters.VEC_2F.writeJson(value.textureScale).ifPresent(tag -> texture.add("scale", tag));
                }

                if(!Arrays.equals(value.textureFaces, Direction.values())) {
                    DIRECTIONS.writeJson(value.textureFaces).ifPresent(tag -> texture.add("faces", tag));
                }

                json.add("texture", texture);
                return json;
            });
        }

        @Override
        public Optional<DynamicCuboid> readJson(JsonElement json) {
            if(!(json instanceof JsonObject object)) {
                return Optional.empty();
            }

            JsonObject texture = object.get("texture").getAsJsonObject();

            return Optional.of(new DynamicCuboid(
                Adapters.VEC_3F.readJson(object.get("offset")).orElseThrow(),
                Adapters.VEC_3F.readJson(object.get("size")).orElseThrow(),
                Adapters.DILATION.readJson(object.get("dilation")).orElse(Dilation.NONE),
                Adapters.BOOLEAN.readJson(object.get("mirror")).orElse(false),
                Adapters.VEC_2F.readJson(texture.get("uv")).orElseThrow(),
                Adapters.VEC_2F.readJson(texture.get("scale")).orElse(new Vector2f(1.0F, 1.0F)),
                DIRECTIONS.readJson(texture.get("faces")).orElse(Direction.values())
            ));
        }
    }

}
