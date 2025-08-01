package abeshutt.staracademy.client;

import abeshutt.staracademy.block.entity.renderer.DynamicOutfit;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.IJsonSerializable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.*;

public class OutfitManager {

    private final Map<String, DynamicOutfit> registry;
    private final Map<UUID, Entry> entries;
    private final Set<UUID> tracked;

    public OutfitManager() {
        this.registry = new HashMap<>();
        this.entries = new HashMap<>();
        this.tracked = new HashSet<>();
    }

    public Map<String, DynamicOutfit> getRegistry() {
        return this.registry;
    }

    public Map<UUID, Entry> getEntries() {
        return this.entries;
    }

    public Set<UUID> getTracked() {
        return this.tracked;
    }

    public void tick(AcademyClient client) {
        if(client.getMinecraft().world == null) {
            if(!this.tracked.isEmpty()) {
                client.send(new UpdateOutfitTrackingPacket(new HashSet<>(), this.tracked));
            }

            return;
        }

        Set<UUID> added = new HashSet<>();
        Set<UUID> removed = new HashSet<>(this.tracked);

        for(AbstractClientPlayerEntity player : client.getMinecraft().world.getPlayers()) {
            if(!this.tracked.contains(player.getUuid())) {
                added.add(player.getUuid());
            }

            removed.remove(player.getUuid());
        }

        if(!added.isEmpty() && !removed.isEmpty()) {
            client.send(new UpdateOutfitTrackingPacket(added, removed));
        }
    }

    public static class Entry implements IJsonSerializable<JsonObject> {
        private final Set<String> unlocked;
        private final Set<String> equipped;

        public Entry() {
            this.unlocked = new HashSet<>();
            this.equipped = new HashSet<>();
        }

        public Entry(Set<String> unlocked, Set<String> equipped) {
            this.unlocked = unlocked;
            this.equipped = equipped;
        }

        @Override
        public Optional<JsonObject> writeJson() {
            return Optional.of(new JsonObject()).map(object -> {
                JsonArray unlocked = new JsonArray();
                JsonArray equipped = new JsonArray();

                for(String uuid : this.unlocked) {
                    Adapters.UTF_8.writeJson(uuid).ifPresent(unlocked::add);
                }

                for(String uuid : this.equipped) {
                    Adapters.UTF_8.writeJson(uuid).ifPresent(equipped::add);
                }

                object.add("unlocked", unlocked);
                object.add("equipped", equipped);
                return object;
            });
        }

        @Override
        public void readJson(JsonObject json) {
            this.unlocked.clear();
            this.equipped.clear();

            if(json.get("unlocked") instanceof JsonArray unlocked) {
                unlocked.forEach(uuid -> Adapters.UTF_8.readJson(uuid).ifPresent(this.unlocked::add));
            }

            if(json.get("equipped") instanceof JsonArray equipped) {
                equipped.forEach(uuid -> Adapters.UTF_8.readJson(uuid).ifPresent(this.equipped::add));
            }
        }
    }

}
