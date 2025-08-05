package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.nbt.NbtElement;
import org.joml.Vector3f;

import java.util.*;

public class DynamicBone {

    public static final Vector3f ZERO = new Vector3f(0.0F, 0.0F, 0.0F);

    public final List<DynamicCuboid> cuboids;
    public final Vector3f rotation;
    public final Vector3f pivot;
    public final Map<String, DynamicBone> children;

    public DynamicBone(List<DynamicCuboid> cuboids, Vector3f rotation, Vector3f pivot, Map<String, DynamicBone> children) {
        this.cuboids = cuboids;
        this.rotation = rotation;
        this.pivot = pivot;
        this.children = children;
    }

    public ModelPartData build() {
        List<ModelCuboidData> vanillaCuboids = new ArrayList<>();

        for(DynamicCuboid cuboid : this.cuboids) {
            vanillaCuboids.add(cuboid.build());
        }

        ModelTransform vanillaTransform = ModelTransform.of(this.pivot.x, this.pivot.y,
                this.pivot.z, this.rotation.x, this.rotation.y, this.rotation.z);

        ModelPartData vanillaBone = new ModelPartData(vanillaCuboids, vanillaTransform);

        this.children.forEach((name, child) -> {
            vanillaBone.children.put(name, child.build());
        });

        return vanillaBone;
    }

    public static class Adapter implements ISimpleAdapter<DynamicBone, NbtElement, JsonElement> {
        @Override
        public Optional<JsonElement> writeJson(DynamicBone value) {
            return Optional.of(new JsonObject()).map(json -> {
                if(!value.cuboids.isEmpty()) {
                    JsonArray cuboids = new JsonArray();

                    for(DynamicCuboid cuboid : value.cuboids) {
                        Adapters.DYNAMIC_CUBOID.writeJson(cuboid).ifPresent(cuboids::add);
                    }

                    json.add("cuboids", cuboids);
                }

                if(!value.rotation.equals(ZERO)) {
                    Adapters.VEC_3F.writeJson(value.rotation).ifPresent(tag -> json.add("rotation", tag));
                }

                if(!value.pivot.equals(ZERO)) {
                    Adapters.VEC_3F.writeJson(value.pivot).ifPresent(tag -> json.add("pivot", tag));
                }

                value.children.forEach((name, child) -> {
                    Adapters.DYNAMIC_BONE.writeJson(child).ifPresent(object -> {
                        json.add("/" + name, object);
                    });
                });

                return json;
            });
        }

        @Override
        public Optional<DynamicBone> readJson(JsonElement json) {
            if(!(json instanceof JsonObject object)) {
                return Optional.empty();
            }

            List<DynamicCuboid> cuboids = new ArrayList<>();
            Map<String, DynamicBone> children = new LinkedHashMap<>();

            if(object.get("cuboids") instanceof JsonArray array) {
                for(JsonElement element : array) {
                    Adapters.DYNAMIC_CUBOID.readJson(element).ifPresent(cuboids::add);
                }
            }

            for(String key : object.keySet()) {
               if(key.charAt(0) != '/') continue;

               Adapters.DYNAMIC_BONE.readJson(object.get(key)).ifPresent(child -> {
                   children.put(key.substring(1), child);
               });
            }

            return Optional.of(new DynamicBone(
                cuboids,
                Adapters.VEC_3F.readJson(object.get("rotation")).orElse(ZERO),
                Adapters.VEC_3F.readJson(object.get("pivot")).orElse(ZERO),
                children
            ));
        }
    }

}
