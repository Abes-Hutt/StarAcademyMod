package abeshutt.staracademy.client;

import abeshutt.staracademy.block.entity.renderer.DynamicOutfit;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.IJsonSerializable;
import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OutfitManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final AcademyClient client;
    private final Map<String, DynamicOutfit> registry;
    private final Map<UUID, Entry> entries;
    private final Set<UUID> tracked;

    public OutfitManager(AcademyClient client) {
        this.client = client;
        this.registry = new HashMap<>();
        this.entries = new HashMap<>();
        this.tracked = new HashSet<>();

        Path path = Paths.get("codex", "outfits");

        if(Files.exists(path)) {
            try {
                Files.list(path).forEach(child -> {
                    if(Files.isDirectory(child)) return;
                    if(!child.toString().endsWith(".json")) return;
                    String id = child.getFileName().toString().replace(".json", "");

                    try {
                        JsonElement json = JsonParser.parseString(Files.readString(child));

                        Adapters.DYNAMIC_OUTFIT.readJson(json).ifPresent(outfit -> {
                            this.registry.put(id, outfit);
                        });
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
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

    public Set<String> getEquipped(UUID uuid) {
        if(this.entries.containsKey(uuid)) {
            return this.entries.get(uuid).equipped;
        }

        return new HashSet<>();
    }

    public void receive(Map<String, DynamicOutfit> registry) {
        this.registry.clear();
        this.registry.putAll(registry);

        this.registry.forEach((id, outfit) -> {
            Adapters.DYNAMIC_OUTFIT.writeJson(outfit).ifPresent(tag -> {
                String json = GSON.toJson(tag);
                Path path = Paths.get("codex", "outfits", id + ".json");

                try {
                    Files.createDirectories(path.getParent());
                    Files.writeString(path, json);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });
        });
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

    public boolean isUnlocked(UUID uuid, String id) {
        Entry entry = this.entries.get(uuid);

        if(entry != null) {
            return entry.unlocked.contains(id);
        }

        return false;
    }

    public boolean isEquipped(UUID uuid, String id) {
        Entry entry = this.entries.get(uuid);

        if(entry != null) {
            return entry.equipped.contains(id);
        }

        return false;
    }

    public void setEquipped(String id, boolean equipped) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        Entry entry = this.entries.get(player.getUuid());

        if(entry != null) {
            if(equipped) {
                entry.equipped.add(id);
            } else {
                entry.equipped.remove(id);
            }

            this.client.send(new UpdateOutfitEntryPacket(Map.of(player.getUuid(), entry)));
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

        public Set<String> getUnlocked() {
            return this.unlocked;
        }

        public Set<String> getEquipped() {
            return this.equipped;
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
