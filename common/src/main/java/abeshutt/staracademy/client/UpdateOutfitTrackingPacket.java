package abeshutt.staracademy.client;

import abeshutt.staracademy.data.adapter.Adapters;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UpdateOutfitTrackingPacket extends AcademyPacket {

    private final Set<UUID> added;
    private final Set<UUID> removed;

    public UpdateOutfitTrackingPacket() {
        this.added = new HashSet<>();
        this.removed = new HashSet<>();
    }

    public UpdateOutfitTrackingPacket(Set<UUID> added, Set<UUID> removed) {
        this.added = added;
        this.removed = removed;
    }

    public Set<UUID> getAdded() {
        return this.added;
    }

    public Set<UUID> getRemoved() {
        return this.removed;
    }

    @Override
    public Optional<JsonObject> writeJson() {
        return super.writeJson().map(object -> {
            JsonArray added = new JsonArray();
            JsonArray removed = new JsonArray();

            for(UUID uuid : this.added) {
                Adapters.UUID.writeJson(uuid).ifPresent(added::add);
            }

            for(UUID uuid : this.removed) {
                Adapters.UUID.writeJson(uuid).ifPresent(removed::add);
            }

            object.add("added", added);
            object.add("removed", removed);
            return object;
        });
    }

    @Override
    public void readJson(JsonObject json) {
        super.readJson(json);
        this.added.clear();
        this.removed.clear();

        if(json.get("added") instanceof JsonArray added) {
            added.forEach(uuid -> Adapters.UUID.readJson(uuid).ifPresent(this.added::add));
        }

        if(json.get("removed") instanceof JsonArray removed) {
            removed.forEach(uuid -> Adapters.UUID.readJson(uuid).ifPresent(this.removed::add));
        }
    }

}
