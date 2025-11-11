package abeshutt.staracademy.api.packet;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.api.OutfitManager;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UpdateOutfitEntryPacket extends AcademyPacket {

    private final Map<UUID, OutfitManager.Entry> entries;

    public UpdateOutfitEntryPacket() {
        this.entries = new HashMap<>();
    }

    public UpdateOutfitEntryPacket(Map<UUID, OutfitManager.Entry> entries) {
        this.entries = entries;
    }

    public Map<UUID, OutfitManager.Entry> getEntries() {
        return this.entries;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            JsonObject entries = new JsonObject();

            this.entries.forEach((uuid, entry) -> {
                entry.writeJson().ifPresent(tag -> entries.add(uuid.toString(), tag));
            });

            object.add("entries", entries);
            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.entries.clear();

        if(json.get("entries") instanceof JsonObject object) {
            for(String key : object.keySet()) {
                if(!(object.get(key) instanceof JsonObject tag)) {
                    continue;
                }

                try {
                    OutfitManager.Entry entry = new OutfitManager.Entry();
                    entry.readJson(tag);
                    this.entries.put(UUID.fromString(key), entry);
                } catch(Exception e) {
                    StarAcademyMod.LOGGER.error("Failed to read UUID {}.", key, e);
                }
            }
        }
    }

}
