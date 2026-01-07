package abeshutt.staracademy.live.api.packet;

import abeshutt.staracademy.live.api.adapter.JsonAdapter;
import abeshutt.staracademy.live.api.dto.CosmeticData;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateCosmeticsPacket extends Packet {

    private final Map<UUID, CosmeticData> entries;
    private boolean delta;

    public UpdateCosmeticsPacket() {
        this.entries = new LinkedHashMap<>();
        this.delta = false;
    }

    public UpdateCosmeticsPacket(Map<UUID, CosmeticData> entries, boolean delta) {
        this.entries = entries;
        this.delta = delta;
    }

    public Map<UUID, CosmeticData> getEntries() {
        return this.entries;
    }

    public boolean isDelta() {
        return this.delta;
    }

    @Override
    public void writeJson(JsonAdapter adapter, JsonObject json) {
        super.writeJson(adapter, json);
        JsonObject entries = new JsonObject();

        this.entries.forEach((uuid, entry) -> {
            entries.add(uuid.toString(), adapter.writeSerializable(entry));
        });

        json.add("entries", entries);
        json.add("delta", adapter.writeBoolean(this.delta));
    }

    @Override
    public void readJson(JsonAdapter adapter, JsonObject json) {
        super.readJson(adapter, json);
        this.entries.clear();

        if (json.get("entries") instanceof JsonObject entries) {
            entries.keySet().forEach(key -> {
                try {
                    UUID uuid = UUID.fromString(key);
                    adapter.readSerializable(entries.get(key), CosmeticData::new)
                            .ifPresent(entry -> this.entries.put(uuid, entry));
                } catch (Exception ignored) { }
            });
        }

        this.delta = adapter.readBoolean(json.get("delta")).orElse(false);
    }

}
