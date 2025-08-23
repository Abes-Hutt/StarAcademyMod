package abeshutt.staracademy.outfit;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.array.ArrayAdapter;
import abeshutt.staracademy.data.adapter.basic.EnumAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class OutfitPieceExporter {

    private static final ArrayAdapter<Direction> DIRECTIONS = Adapters.ofArray(Direction[]::new,
            Adapters.ofEnum(Direction.class, EnumAdapter.Mode.NAME));

    public static JsonObject meshJson(ModelPartData modelPartData) {
        JsonObject meshJson = new JsonObject();

        if (!(modelPartData.rotationData.pivotX == 0
                && modelPartData.rotationData.pivotY == 0
                && modelPartData.rotationData.pivotZ == 0)) {
            JsonArray pivot = new JsonArray();
            pivot.add(modelPartData.rotationData.pivotX);
            pivot.add(modelPartData.rotationData.pivotY);
            pivot.add(modelPartData.rotationData.pivotZ);
            meshJson.add("pivot", pivot);
        }

        if (!(modelPartData.rotationData.pitch == 0
                && modelPartData.rotationData.yaw == 0
                && modelPartData.rotationData.roll == 0)) {
            JsonArray rotation = new JsonArray();
            rotation.add(modelPartData.rotationData.pitch);
            rotation.add(modelPartData.rotationData.yaw);
            rotation.add(modelPartData.rotationData.roll);
            meshJson.add("rotation", rotation);
        }

        JsonArray cuboids = new JsonArray();
        for (ModelCuboidData cuboidData : modelPartData.cuboidData) {
            JsonObject cuboidJson = new JsonObject();

            JsonArray offset = new JsonArray();
            offset.add(cuboidData.offset.x);
            offset.add(cuboidData.offset.y);
            offset.add(cuboidData.offset.z);
            cuboidJson.add("offset", offset);

            JsonArray size = new JsonArray();
            size.add(cuboidData.dimensions.x);
            size.add(cuboidData.dimensions.y);
            size.add(cuboidData.dimensions.z);
            cuboidJson.add("size", size);

            if (!(cuboidData.extraSize.radiusX == 0
                    && cuboidData.extraSize.radiusY == 0
                    && cuboidData.extraSize.radiusZ == 0)) {
                JsonArray dilation = new JsonArray();
                dilation.add(cuboidData.extraSize.radiusX);
                dilation.add(cuboidData.extraSize.radiusY);
                dilation.add(cuboidData.extraSize.radiusZ);
                cuboidJson.add("dilation", dilation);
            }

            if (cuboidData.mirror) {
                cuboidJson.addProperty("mirror", true);
            }

            JsonObject texture = new JsonObject();
            JsonArray textureUv = new JsonArray();
            JsonArray textureScale = new JsonArray();
            textureUv.add(cuboidData.textureUV.getX());
            textureUv.add(cuboidData.textureUV.getY());
            texture.add("uv", textureUv);
            if (!(cuboidData.textureScale.getX() == 1 && cuboidData.textureScale.getY() == 1)) {
                textureScale.add(cuboidData.textureScale.getX());
                textureScale.add(cuboidData.textureScale.getY());
                texture.add("scale", textureScale);
            }
            if (!Arrays.equals(cuboidData.directions.toArray(Direction[]::new), Direction.values())) {
                DIRECTIONS.writeJson(cuboidData.directions.toArray(Direction[]::new))
                        .ifPresent(tag -> texture.add("faces", tag));
            }
            cuboidJson.add("texture", texture);

            cuboids.add(cuboidJson);
        }

        if (!cuboids.isEmpty()) {
            meshJson.add("cuboids", cuboids);
        }

        modelPartData.children.forEach((key, child) -> {
            JsonObject childMeshJson = meshJson(child);
            if (childMeshJson.isEmpty()) return;
            meshJson.add("/" + key, childMeshJson);
        });

        return meshJson;
    }

}
