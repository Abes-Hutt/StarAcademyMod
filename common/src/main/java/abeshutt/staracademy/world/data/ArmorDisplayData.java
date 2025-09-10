package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateArmorDisplayS2CPacket;
import com.google.gson.JsonObject;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class ArmorDisplayData extends WorldData {

    public static final ArmorDisplayData CLIENT = new ArmorDisplayData();

    private final Map<UUID, Entry> entries;

    public ArmorDisplayData() {
        this.entries = new LinkedHashMap<>();
    }

    public Map<UUID, Entry> getEntries() {
        return this.entries;
    }

    public boolean isHidden(UUID uuid, EquipmentSlot slot) {
        Entry entry = this.entries.get(uuid);
        if(entry == null) return false;
        return entry.getHidden().contains(slot);
    }

    public void toggle(UUID uuid, EquipmentSlot slot) {
        Entry entry = this.entries.computeIfAbsent(uuid, key -> new Entry());

        if(entry.getHidden().contains(slot)) {
            entry.getHidden().remove(slot);
        } else {
            entry.getHidden().add(slot);
        }

        entry.setDirty(true);
        this.markDirty();
    }

    private void onJoin(ServerPlayerEntity player) {
        NetworkManager.sendToPlayer(player, new UpdateArmorDisplayS2CPacket(null));
        NetworkManager.sendToPlayer(player, new UpdateArmorDisplayS2CPacket(this.entries));
    }

    public void onTick(MinecraftServer server) {
        Map<UUID, ArmorDisplayData.Entry> updated = new HashMap<>();

        this.entries.forEach((uuid, entry) -> {
            if(entry.isDirty()) {
                updated.put(uuid, entry);
                entry.setDirty(false);
            }
        });

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            NetworkManager.sendToPlayer(player, new UpdateArmorDisplayS2CPacket(updated));
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

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            ArmorDisplayData data = ModWorldData.ARMOR_DISPLAY.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            ArmorDisplayData data = ModWorldData.ARMOR_DISPLAY.getGlobal(server);
            data.onTick(server);
        });
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private final Set<EquipmentSlot> hidden;
        private boolean dirty;

        public Entry() {
            this.hidden = new LinkedHashSet<>();
        }

        public Entry(Collection<EquipmentSlot> hidden) {
            this.hidden = new LinkedHashSet<>(hidden);
        }

        public Set<EquipmentSlot> getHidden() {
            return this.hidden;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.INT_SEGMENTED_3.writeBits(this.hidden.size(), buffer);

            for(EquipmentSlot slot : this.hidden) {
                Adapters.UTF_8.writeBits(slot.getName(), buffer);
            }
        }

        @Override
        public void readBits(BitBuffer buffer) {
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
            this.hidden.clear();

            for(int i = 0; i < size; i++) {
                this.hidden.add(EquipmentSlot.byName(Adapters.UTF_8.readBits(buffer).orElseThrow()));
            }
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                NbtList hidden = new NbtList();

                for(EquipmentSlot slot : this.hidden) {
                    Adapters.UTF_8.writeNbt(slot.getName()).ifPresent(hidden::add);
                }

                nbt.put("hidden", hidden);
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.hidden.clear();

            if(nbt.get("hidden") instanceof NbtList options) {
                for(NbtElement option : options) {
                    Adapters.UTF_8.readNbt(option).map(EquipmentSlot::byName).ifPresent(this.hidden::add);
                }
            }
        }
    }

}
