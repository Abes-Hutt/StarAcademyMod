package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public class DynamicOutfit {

    public final String name;
    public final ModelIdentifier icon;

    public final DynamicTexture texture;
    public final DynamicBone mesh;

    public DynamicOutfit(String name, ModelIdentifier icon, DynamicTexture texture, DynamicBone mesh) {
        this.name = name;
        this.icon = icon;
        this.texture = texture;
        this.mesh = mesh;
    }

    public static class Adapter implements ISimpleAdapter<DynamicOutfit, NbtElement, JsonElement> {
        @Override
        public Optional<JsonElement> writeJson(DynamicOutfit value) {
            return Optional.of(new JsonObject()).map(object -> {
                Adapters.UTF_8.writeJson(value.name).ifPresent(tag -> object.add("name", tag));
                Adapters.MODEL_IDENTIFIER.writeJson(value.icon).ifPresent(tag -> object.add("icon", tag));
                Adapters.DYNAMIC_TEXTURE.writeJson(value.texture).ifPresent(tag -> object.add("texture", tag));
                Adapters.DYNAMIC_BONE.writeJson(value.mesh).ifPresent(tag -> object.add("mesh", tag));
                return object;
            });
        }

        @Override
        public Optional<DynamicOutfit> readJson(JsonElement json) {
            if(!(json instanceof JsonObject object)) {
                return Optional.empty();
            }

            return Optional.of(new DynamicOutfit(
                Adapters.UTF_8.readJson(object.get("name")).orElseThrow(),
                Adapters.MODEL_IDENTIFIER.readJson(object.get("icon")).orElseThrow(),
                Adapters.DYNAMIC_TEXTURE.readJson(object.get("texture")).orElseThrow(),
                Adapters.DYNAMIC_BONE.readJson(object.get("mesh")).orElseThrow()
            ));
        }
    }

}
