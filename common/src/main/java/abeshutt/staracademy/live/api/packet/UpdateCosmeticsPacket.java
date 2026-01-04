package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.dto.CosmeticSlot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class UpdateCosmeticsPacket extends Packet {
    
    private boolean delta;
    private final Map<CosmeticSlot, String> slots;
    private final Set<String> unlocked;

    public UpdateCosmeticsPacket() {
        this.delta = false;
        this.slots = new LinkedHashMap<>();
        this.unlocked = new LinkedHashSet<>();
    }

    public UpdateCosmeticsPacket(boolean delta, Map<CosmeticSlot, String> slots, Set<String> unlocked) {
        this.delta = delta;
        this.slots = new LinkedHashMap<>(slots);
        this.unlocked = new LinkedHashSet<>(unlocked);
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        json.add("delta", adapter.writeBoolean(this.delta));

        JsonObject slots = new JsonObject();

        this.slots.forEach((slot, cosmetic) -> {
            if (cosmetic == null) return;
            slots.add(slot.getId(), adapter.writeString(cosmetic));
        });

        json.add("slots", slots);

        JsonArray unlocked = new JsonArray();

        for (String cosmetic : this.unlocked) {
            unlocked.add(adapter.writeString(cosmetic));
        }

        json.add("unlocked", unlocked);
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.delta = adapter.readBoolean(json.get("delta")).orElse(false);

        this.slots.clear();

        if (json.get("slots") instanceof JsonObject slots) {
            for (String key : slots.keySet()) {
                CosmeticSlot.fromId(key).ifPresent(slot -> {
                    adapter.readString(slots.get(key)).ifPresent(cosmetic -> {
                        this.slots.put(slot, cosmetic);
                    });
                });
            }
        }

        this.unlocked.clear();

        if (json.get("unlocked") instanceof JsonArray unlocked) {
            for (JsonElement cosmeticId : unlocked) {
                adapter.readString(cosmeticId).ifPresent(this.unlocked::add);
            }
        }
    }

}
