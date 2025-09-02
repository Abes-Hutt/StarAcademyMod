package abeshutt.staracademy.world.data;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.WorldKeysUpdateS2CPacket;
import abeshutt.staracademy.world.VirtualWorld;
import com.google.gson.JsonObject;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class VirtualWorldData extends WorldData {

    private final Map<Identifier, Entry> entries;

    public VirtualWorldData() {
        this.entries = new LinkedHashMap<>();
    }

    public VirtualWorld instantiate(MinecraftServer server, Identifier id, Set<String> tags) {
        if(tags.contains("island")) {
            return VirtualWorld.create(server, id, server.getCombinedDynamicRegistries()
                    .getCombinedRegistryManager()
                    .get(RegistryKeys.DIMENSION)
                    .get(RegistryKey.of(RegistryKeys.DIMENSION, StarAcademyMod.id("island"))));
        }

        throw new UnsupportedOperationException();
    }

    public boolean has(Identifier id) {
        return this.entries.containsKey(id);
    }

    public Optional<VirtualWorld> get(MinecraftServer server, Identifier id) {
        Entry entry = this.entries.get(id);

        if(entry != null) {
            if(entry.getWorld() != null) {
                return Optional.of(entry.getWorld());
            }

            VirtualWorld world = this.instantiate(server, id, entry.getTags());
            entry.setWorld(world);
            this.load(world);
            return Optional.of(world);
        }

        return Optional.empty();
    }

    public void add(Identifier id, String... tags) {
        Entry entry = this.entries.get(id);

        if(entry != null && entry.getWorld() != null) {
            this.unload(entry.getWorld());
        }

        this.entries.put(id, new Entry(false, tags));
        this.markDirty();
    }

    public void remove(Identifier id) {
        Entry entry = this.entries.remove(id);

        if(entry != null && entry.getWorld() != null) {
            this.unload(entry.getWorld());
        }

        if(entry != null) {
            this.markDirty();
        }
    }

    public void load(VirtualWorld world) {
        //TODO: neoforge uses worldArray instead of worlds
        world.getServer().worlds.put(world.getRegistryKey(), world);
        LifecycleEvent.SERVER_LEVEL_LOAD.invoker().act(world);

        for(ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            NetworkManager.sendToPlayer(player, new WorldKeysUpdateS2CPacket(Set.of(world.getId()), Set.of()));
        }
    }

    public void unload(VirtualWorld world) {
        world.getServer().worlds.remove(world.getRegistryKey());
        LifecycleEvent.SERVER_LEVEL_UNLOAD.invoker().act(world);

        for(ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            NetworkManager.sendToPlayer(player, new WorldKeysUpdateS2CPacket(Set.of(), Set.of(world.getId())));
        }
    }

    public void onTick(MinecraftServer server) {
        if(ModConfigs.ISLAND.isEnabled()) {
            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Identifier id = VirtualWorld.island(player);
                if(this.has(id)) continue;
                this.add(id, "island");
            }
        }

        this.entries.forEach((id, entry) -> {
            this.get(server, id);
        });
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtCompound entries = new NbtCompound();

            this.entries.forEach((id, entry) -> {
                entry.writeNbt().ifPresent(compound -> {
                    entries.put(id.toString(), compound);
                });
            });

            nbt.put("entries", entries);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.entries.clear();

        if(nbt.get("entries") instanceof NbtCompound entries) {
            for(String key : entries.getKeys()) {
                Entry entry = new Entry();
                entry.readNbt(entries.getCompound(key));
                this.entries.put(Identifier.of(key), entry);
            }
        }
    }

    public static void init() {
        TickEvent.SERVER_POST.register(server -> {
            VirtualWorldData data = ModWorldData.VIRTUAL_WORLD.getGlobal(server);
            data.onTick(server);
        });
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private VirtualWorld world;
        private boolean deleted;
        private final Set<String> tags;

        public Entry() {
            this.tags = new LinkedHashSet<>();
        }

        public Entry(boolean deleted, String... tags) {
            this.deleted = deleted;
            this.tags = new LinkedHashSet<>();
            this.tags.addAll(Arrays.asList(tags));
        }

        public VirtualWorld getWorld() {
            return this.world;
        }

        public Entry setWorld(VirtualWorld world) {
            this.world = world;
            return this;
        }

        public boolean isDeleted() {
            return this.deleted;
        }

        public Set<String> getTags() {
            return tags;
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.BOOLEAN.writeNbt(this.deleted).ifPresent(tag -> nbt.put("deleted", tag));
                NbtList tags = new NbtList();

                for(String tag : this.tags) {
                    Adapters.UTF_8.writeNbt(tag).ifPresent(tags::add);
                }

                nbt.put("tags", tags);
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.deleted = Adapters.BOOLEAN.readNbt(nbt.get("deleted")).orElseThrow();
            this.tags.clear();

            if(nbt.get("tags") instanceof NbtList tags) {
                for(NbtElement tag : tags) {
                   Adapters.UTF_8.readNbt(tag).ifPresent(this.tags::add);
                }
            }
        }
    }

}
