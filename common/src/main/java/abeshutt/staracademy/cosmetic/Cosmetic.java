package abeshutt.staracademy.cosmetic;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.IJsonSerializable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class Cosmetic implements IJsonSerializable<JsonElement> {

    private String id;
    private String name;
    private String description;
    private Identifier model;
    private Identifier animation;
    private Identifier texture;
    private final Set<String> slots;

    public Cosmetic() {
        this.slots = new LinkedHashSet<>();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Identifier getModel() {
        return this.model;
    }

    public Identifier getAnimation() {
        return this.animation;
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public Set<String> getSlots() {
        return this.slots;
    }

    @Override
    public Optional<JsonElement> writeJson() {
        return Optional.of(new JsonObject()).map(json -> {
            Adapters.UTF_8.writeJson(this.id).ifPresent(value -> json.add("id", value));
            Adapters.UTF_8.writeJson(this.name).ifPresent(value -> json.add("name", value));
            Adapters.UTF_8.writeJson(this.description).ifPresent(value -> json.add("description", value));
            Adapters.IDENTIFIER.writeJson(this.model).ifPresent(value -> json.add("model", value));
            Adapters.IDENTIFIER.writeJson(this.animation).ifPresent(value -> json.add("animation", value));
            Adapters.IDENTIFIER.writeJson(this.texture).ifPresent(value -> json.add("texture", value));
            JsonArray slots = new JsonArray();

            for (String slot : this.slots) {
                Adapters.UTF_8.writeJson(slot).ifPresent(value -> slots.add(slot));
            }

            json.add("slots", slots);
            return json;
        });
    }

    @Override
    public void readJson(JsonElement json) {
        if (json instanceof JsonObject object) {
            this.id = Adapters.UTF_8.readJson(object.get("id")).orElse(null);
            this.name = Adapters.UTF_8.readJson(object.get("name")).orElse(null);
            this.description = Adapters.UTF_8.readJson(object.get("description")).orElse(null);
            this.model = Adapters.IDENTIFIER.readJson(object.get("model")).orElse(null);
            this.animation = Adapters.IDENTIFIER.readJson(object.get("animation")).orElse(null);
            this.texture = Adapters.IDENTIFIER.readJson(object.get("texture")).orElse(null);
            this.slots.clear();

            if (object.get("slots") instanceof JsonArray slots) {
                for (JsonElement slot : slots) {
                    Adapters.UTF_8.readJson(slot).ifPresent(this.slots::add);
                }
            }
        } else {
            this.id = null;
            this.name = null;
            this.description = null;
            this.model = null;
            this.animation = null;
            this.texture = null;
            this.slots.clear();
        }
    }

}
