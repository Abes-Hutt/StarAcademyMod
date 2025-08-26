package abeshutt.staracademy.outfit.core;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.outfit.OutfitPieceExporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Deprecated(forRemoval = false)
public abstract class OutfitPiece {

    private static final String[] BASE_PARTS = {"head", "hat", "body", "right_arm", "right_leg", "right_sleeve",
            "right_pants", "left_arm", "left_leg", "left_sleeve", "left_pants", "ear", "cloak", "jacket"};

    private final String id;
    private int order;
    private OutfitTexture texture;

    @Environment(EnvType.CLIENT)
    private OutfitModel model;

    @Environment(EnvType.CLIENT)
    private JsonObject classicModelJson;

    public OutfitPiece(String id) {
        this.id = id;

        if (Platform.getEnvironment() == Env.CLIENT) {
            this.classicModelJson = new JsonObject();

            this.texture = this.buildTexture();
            this.model = new OutfitModel(this.createMesh().createModel());

            JsonObject textureJson = new JsonObject();
            textureJson.addProperty("id", this.texture.model.toString());
            textureJson.addProperty("width", this.texture.width);
            textureJson.addProperty("height", this.texture.height);

            this.classicModelJson.add("texture", textureJson);

            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("name", "item.academy.outfit." + this.id);
            modelJson.addProperty("icon", this.texture.icon.toString());
            modelJson.add("classic", this.classicModelJson);

            File jsonFile = Path.of("outfit_dump", this.id + ".json").toFile();

            try {
                jsonFile.getParentFile().mkdirs();
                jsonFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (Writer writer = new FileWriter(jsonFile, StandardCharsets.UTF_8)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(modelJson, writer);

            } catch (IOException e) {
                StarAcademyMod.LOGGER.error("Failed to write outfit dump for {} - {}",
                        this.id, e.getMessage());
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public int getOrder() {
        return order;
    }

    public OutfitTexture getTexture() {
        return this.texture;
    }

    public OutfitModel getModel() {
        return this.model;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Environment(EnvType.CLIENT)
    protected abstract OutfitTexture buildTexture();

    @Environment(EnvType.CLIENT)
    protected TexturedModelData createMesh() {
        ModelData mesh = new ModelData();
        ModelPartData root = mesh.getRoot();

        for (String name : BASE_PARTS) {
            root.addChild(name, ModelPartBuilder.create(),
                    ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        }

        this.buildMesh(root);

        JsonObject meshJson = OutfitPieceExporter.meshJson(root);
        this.classicModelJson.add("mesh", meshJson);

        return TexturedModelData.of(mesh, this.texture.getWidth(), this.texture.getHeight());
    }

    @Environment(EnvType.CLIENT)
    protected abstract void buildMesh(ModelPartData root);

}