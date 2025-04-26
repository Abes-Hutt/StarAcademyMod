package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModNetwork;
import abeshutt.staracademy.init.ModOutfits;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateOutfitS2CPacket;
import abeshutt.staracademy.outfit.core.OutfitPiece;
import com.google.gson.JsonObject;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class WardrobeData extends WorldData {

    public static final WardrobeData CLIENT = new WardrobeData();

    private final Map<UUID, Entry> entries;

    public WardrobeData() {
        this.entries = new HashMap<>();
    }

    public Map<UUID, Entry> getEntries() {
        return this.entries;
    }

    public Optional<Entry> get(UUID uuid) {
        return Optional.ofNullable(this.entries.get(uuid));
    }

    public Entry getOrCreate(UUID uuid) {
        return this.entries.computeIfAbsent(uuid, key -> new Entry());
    }

    public boolean isUnlocked(UUID uuid, String id) {
        return this.get(uuid).map(entry -> entry.getUnlocked().contains(id)).orElse(false);
    }

    public boolean isEquipped(UUID uuid, String id) {
        return this.get(uuid).map(entry -> entry.getEquipped().contains(id)).orElse(false);
    }

    public boolean setUnlocked(ServerPlayerEntity player, String id, boolean unlocked) {
        if(this.setUnlocked(player.getUuid(), id, unlocked)) {
            OutfitPiece outfit = ModOutfits.REGISTRY.get(id);

            if(player.getServer() != null && outfit != null) {
                for(ServerPlayerEntity other : player.getServer().getPlayerManager().getPlayerList()) {
                    other.sendMessage(Text.empty()
                        .append(player.getName())
                        .append(Text.literal(" unlocked a new outfit: ").formatted(Formatting.GRAY))
                        .append(Text.translatable("item.academy.outfit." + outfit.getId()))
                        .append(Text.literal(".").formatted(Formatting.GRAY)));
                }
            }

            return true;
        }

        return false;
    }

    public boolean setUnlocked(UUID uuid, String id, boolean unlocked) {
        Entry entry = this.getOrCreate(uuid);

        if((unlocked && entry.getUnlocked().add(id)) || (!unlocked && entry.getUnlocked().remove(id))) {
            entry.setDirty(true);
            this.setDirty(true);
            return true;
        }

        return false;
    }

    public boolean setEquipped(UUID uuid, String id, boolean equipped) {
        Entry entry = this.getOrCreate(uuid);

        if(equipped && !entry.getUnlocked().contains(id)) {
            return false;
        }

        if((equipped && entry.getEquipped().add(id)) || (!equipped && entry.getEquipped().remove(id))) {
            entry.setDirty(true);
            this.setDirty(true);
            return true;
        }

        return false;
    }

    private void onJoin(ServerPlayerEntity player) {
        NetworkManager.sendToPlayer(player, new UpdateOutfitS2CPacket(player.getUuid(), this.getOrCreate(player.getUuid())));
    }

    public void onTick(MinecraftServer server) {
        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            this.get(player.getUuid()).ifPresent(entry -> {
                entry.getEquipped().removeIf(s -> {
                    if(!entry.getUnlocked().contains(s)) {
                        entry.setDirty(true);
                        this.setDirty(true);
                        return true;
                    }

                    return false;
                });
            });
        }

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            this.get(player.getUuid()).ifPresent(entry -> {
                if(!entry.isDirty()) return;
                NetworkManager.sendToPlayers(server.getPlayerManager().getPlayerList(),
                        new UpdateOutfitS2CPacket(player.getUuid(), entry));
                entry.setDirty(false);
            });
        }
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtCompound entries = new NbtCompound();

            this.entries.forEach((uuid, entry) -> {
                entry.writeNbt().ifPresent(tag -> entries.put(uuid.toString(), tag));
            });

            nbt.put("entries", entries);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.entries.clear();
        NbtCompound entries = nbt.getCompound("entries");

        for(String key : entries.getKeys()) {
           Entry entry = new Entry();
           entry.readNbt(entries.getCompound(key));
           this.entries.put(UUID.fromString(key), entry);
        }
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private final Set<String> unlocked;
        private final Set<String> equipped;
        private boolean dirty;

        public Entry() {
            this.unlocked = new HashSet<>();
            this.equipped = new HashSet<>();
        }

        public Set<String> getUnlocked() {
            return this.unlocked;
        }

        public Set<String> getEquipped() {
            return this.equipped;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.INT_SEGMENTED_7.writeBits(this.unlocked.size(), buffer);

            for(String id : this.unlocked) {
               Adapters.UTF_8.writeBits(id, buffer);
            }

            Adapters.INT_SEGMENTED_7.writeBits(this.equipped.size(), buffer);

            for(String id : this.equipped) {
                Adapters.UTF_8.writeBits(id, buffer);
            }
        }

        @Override
        public void readBits(BitBuffer buffer) {
            this.unlocked.clear();
            int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
               Adapters.UTF_8.readBits(buffer).ifPresent(this.unlocked::add);
            }

            this.equipped.clear();
            size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                Adapters.UTF_8.readBits(buffer).ifPresent(this.equipped::add);
            }
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                NbtList unlocked = new NbtList();

                for(String id : this.unlocked) {
                   Adapters.UTF_8.writeNbt(id).ifPresent(unlocked::add);
                }

                nbt.put("unlocked", unlocked);
                NbtList equipped = new NbtList();

                for(String id : this.equipped) {
                    Adapters.UTF_8.writeNbt(id).ifPresent(equipped::add);
                }

                nbt.put("equipped", equipped);
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.unlocked.clear();

            if(nbt.get("unlocked") instanceof NbtList unlocked) {
                for(NbtElement element : unlocked) {
                    Adapters.UTF_8.readNbt(element).ifPresent(this.unlocked::add);
                }
            }

            this.equipped.clear();

            if(nbt.get("equipped") instanceof NbtList equipped) {
                for(NbtElement element : equipped) {
                    Adapters.UTF_8.readNbt(element).ifPresent(this.equipped::add);
                }
            }
        }
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            WardrobeData data = ModWorldData.WARDROBE.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            WardrobeData data = ModWorldData.WARDROBE.getGlobal(server);
            data.onTick(server);
        });
    }

}
