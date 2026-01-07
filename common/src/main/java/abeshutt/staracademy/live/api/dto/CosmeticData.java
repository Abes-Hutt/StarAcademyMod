package abeshutt.staracademy.live.api.dto;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.adapter.JsonSerializable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class CosmeticData implements JsonSerializable {

    private final Map<String, String> slots;
    private final Set<String> unlocked;
    private boolean delta;

    public CosmeticData() {
        this.slots = new LinkedHashMap<>();
        this.unlocked = new LinkedHashSet<>();
        this.delta = false;
    }

    public CosmeticData(Map<String, String> slots, Set<String> unlocked, boolean delta) {
        this.slots = slots;
        this.unlocked = unlocked;
        this.delta = delta;
    }

    public Map<String, String> getSlots() {
        return this.slots;
    }

    public Set<String> getUnlocked() {
        return this.unlocked;
    }

    public boolean isDelta() {
        return this.delta;
    }

    public Optional<String> getCosmeticInSlot(String slot) {
        return Optional.ofNullable(this.slots.get(slot));
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        JsonObject slots = new JsonObject();

        this.slots.forEach((slot, cosmetic) -> {
            if (cosmetic == null) return;
            slots.add(slot, adapter.writeString(cosmetic));
        });

        json.add("slots", slots);

        JsonArray unlocked = new JsonArray();

        for (String cosmetic : this.unlocked) {
            unlocked.add(adapter.writeString(cosmetic));
        }

        json.add("unlocked", unlocked);

        if (this.delta) {
            json.addProperty("delta", true);
        }
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        this.slots.clear();

        if (json.get("slots") instanceof JsonObject slots) {
            for (String slot : slots.keySet()) {
                adapter.readString(slots.get(slot)).ifPresent(cosmetic -> {
                    this.slots.put(slot, cosmetic);
                });
            }
        }

        this.unlocked.clear();

        if (json.get("unlocked") instanceof JsonArray unlocked) {
            for (JsonElement cosmeticId : unlocked) {
                adapter.readString(cosmeticId).ifPresent(this.unlocked::add);
            }
        }

        this.delta = adapter.readBoolean(json.get("delta")).orElse(false);
    }

}
