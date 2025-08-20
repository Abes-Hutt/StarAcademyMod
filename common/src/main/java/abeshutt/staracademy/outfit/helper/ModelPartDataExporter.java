package abeshutt.staracademy.outfit.helper;

import abeshutt.staracademy.init.ModOutfits;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPartData;

public class ModelPartDataExporter {

    public static JsonObject export(ModelPartData modelPartData) {
        JsonObject modelJson = new JsonObject();

        JsonArray pivot = new JsonArray();
        pivot.add(modelPartData.rotationData.pivotX);
        pivot.add(modelPartData.rotationData.pivotY);
        pivot.add(modelPartData.rotationData.pivotZ);
        modelJson.add("pivot", pivot);

        JsonArray rotation = new JsonArray();
        rotation.add(modelPartData.rotationData.pitch);
        rotation.add(modelPartData.rotationData.yaw);
        rotation.add(modelPartData.rotationData.roll);
        modelJson.add("rotation", rotation);

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

            JsonArray dilation = new JsonArray();
            dilation.add(cuboidData.extraSize.radiusX);
            dilation.add(cuboidData.extraSize.radiusY);
            dilation.add(cuboidData.extraSize.radiusZ);
            cuboidJson.add("dilation", dilation);

            JsonObject texture = new JsonObject();
            JsonArray textureUv = new JsonArray();
            JsonArray textureScale = new JsonArray();
            textureUv.add(cuboidData.textureUV.getX());
            textureUv.add(cuboidData.textureUV.getY());
            textureScale.add(cuboidData.textureScale.getX());
            textureScale.add(cuboidData.textureScale.getY());
            texture.add("uv", textureUv);
            texture.add("scale", textureScale);
            cuboidJson.add("texture", texture);

            cuboids.add(cuboidJson);
        }

        JsonObject children = new JsonObject();

        modelPartData.children.forEach((key, child) -> {
            children.add(key, export(child));
        });

        return modelJson;
    }

}