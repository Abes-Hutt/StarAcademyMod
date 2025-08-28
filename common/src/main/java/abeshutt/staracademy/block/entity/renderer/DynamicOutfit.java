package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class DynamicOutfit {

    private static final String[] BASE_PARTS = { "head",  "hat",  "body",  "right_arm",  "right_leg", "right_sleeve",
            "right_pants", "left_arm", "left_leg", "left_sleeve", "left_pants", "ear", "cloak", "jacket" };

    public final String name;
    public final ModelIdentifier icon;

    public final DynamicTexture classicTexture;
    public final DynamicBone classicMesh;

    public final DynamicTexture slimTexture;
    public final DynamicBone slimMesh;

    public DynamicOutfit(String name, ModelIdentifier icon, DynamicTexture classicTexture, DynamicBone classicMesh,
                         DynamicTexture slimTexture, DynamicBone slimMesh) {
        this.name = name;
        this.icon = icon;
        this.classicTexture = classicTexture;
        this.classicMesh = classicMesh;
        this.slimTexture = slimTexture;
        this.slimMesh = slimMesh;
    }

    public Identifier getTexture(boolean slim) {
        return slim ? this.slimTexture.id : this.classicTexture.id;
    }

    @Environment(EnvType.CLIENT)
    public PlayerEntityModel<AbstractClientPlayerEntity> getModel(EntityRendererFactory.Context ctx, boolean slim) {
        DynamicTexture texture = slim ? this.slimTexture : this.classicTexture;
        DynamicBone mesh = slim ? this.slimMesh : this.classicMesh;

        ModelPartData root = mesh.build();

        for(String name : BASE_PARTS) {
            if(root.getChild(name) == null) {
                root.addChild(name, ModelPartBuilder.create(),
                        ModelTransform.pivot(0.0F, 0.0F, 0.0F));
            }
        }

        ModelPart model = root.createPart(texture.width, texture.height);
        return new PlayerEntityModel<>(model, slim);
    }

    public static class Adapter implements ISimpleAdapter<DynamicOutfit, NbtElement, JsonElement> {
        @Override
        public Optional<JsonElement> writeJson(DynamicOutfit value) {
            return Optional.of(new JsonObject()).map(object -> {
                Adapters.UTF_8.writeJson(value.name).ifPresent(tag -> object.add("name", tag));
                Adapters.MODEL_IDENTIFIER.writeJson(value.icon).ifPresent(tag -> object.add("icon", tag));

                JsonObject classic = new JsonObject();
                Adapters.DYNAMIC_TEXTURE.writeJson(value.classicTexture).ifPresent(tag -> classic.add("texture", tag));
                Adapters.DYNAMIC_BONE.writeJson(value.classicMesh).ifPresent(tag -> classic.add("mesh", tag));
                object.add("classic", classic);

                JsonObject slim = new JsonObject();
                Adapters.DYNAMIC_TEXTURE.writeJson(value.slimTexture).ifPresent(tag -> slim.add("texture", tag));
                Adapters.DYNAMIC_BONE.writeJson(value.slimMesh).ifPresent(tag -> slim.add("mesh", tag));
                object.add("slim", slim);
                return object;
            });
        }

        @Override
        public Optional<DynamicOutfit> readJson(JsonElement json) {
            if(!(json instanceof JsonObject object)) {
                return Optional.empty();
            }

            JsonObject classic = object.getAsJsonObject("classic");
            JsonObject slim = object.getAsJsonObject("slim");

            if(classic == null && slim != null) {
                classic = slim;
            } else if(classic != null && slim == null) {
                slim = classic;
            }

            if(classic == null) {
                throw new NullPointerException();
            }

            return Optional.of(new DynamicOutfit(
                Adapters.UTF_8.readJson(object.get("name")).orElseThrow(),
                Adapters.MODEL_IDENTIFIER.readJson(object.get("icon")).orElseThrow(),
                Adapters.DYNAMIC_TEXTURE.readJson(classic.get("texture")).orElseThrow(),
                Adapters.DYNAMIC_BONE.readJson(classic.get("mesh")).orElseThrow(),
                Adapters.DYNAMIC_TEXTURE.readJson(slim.get("texture")).orElseThrow(),
                Adapters.DYNAMIC_BONE.readJson(slim.get("mesh")).orElseThrow()
            ));
        }
    }

}
