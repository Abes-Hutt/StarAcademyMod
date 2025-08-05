package abeshutt.staracademy.client;

import abeshutt.staracademy.block.entity.renderer.DynamicOutfit;
import abeshutt.staracademy.data.adapter.Adapters;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpdateOutfitRegistryPacket extends AcademyPacket {

    private final Map<String, DynamicOutfit> registry;

    public UpdateOutfitRegistryPacket() {
        this.registry = new HashMap<>();
    }

    public UpdateOutfitRegistryPacket(Map<String, DynamicOutfit> registry) {
        this.registry = registry;
    }

    public Map<String, DynamicOutfit> getRegistry() {
        return this.registry;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            JsonObject registry = new JsonObject();

            this.registry.forEach((id, outfit) -> {
                Adapters.DYNAMIC_OUTFIT.writeJson(outfit).ifPresent(tag -> {
                    registry.add(id, tag);
                });
            });

            object.add("registry", registry);
            return registry;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.registry.clear();

        if(json.get("registry") instanceof JsonObject registry) {
            for(String id : registry.keySet()) {
               Adapters.DYNAMIC_OUTFIT.readJson(registry.get(id)).ifPresent(tag -> {
                   this.registry.put(id, tag);
               });
            }
        }
    }

}
