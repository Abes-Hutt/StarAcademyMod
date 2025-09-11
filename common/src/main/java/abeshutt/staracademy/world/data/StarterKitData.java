package abeshutt.staracademy.world.data;

import abeshutt.staracademy.config.StarterKitConfig;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import com.google.gson.JsonObject;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.hooks.item.ItemStackHooks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.WorldEvents;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class StarterKitData extends WorldData {

    private final Map<UUID, StarterKitData.Entry> entries;

    public StarterKitData() {
        this.entries = new LinkedHashMap<>();
    }

    public boolean isGranted(UUID uuid) {
        Entry entry = this.entries.get(uuid);
        if(entry == null) return false;
        return entry.isGranted();
    }

    public void setGranted(UUID uuid, boolean granted) {
        this.entries.computeIfAbsent(uuid, k -> new Entry()).granted = granted;
        this.markDirty();
    }

    private void onJoin(ServerPlayerEntity player) {
        if(!this.isGranted(player.getUuid())) {
            ModConfigs.STARTER_KIT.getEquipment().forEach((slot, stack) -> {
                if(player.getEquippedStack(slot).isEmpty()) {
                    player.equipStack(slot, stack.copy());
                } else {
                    ItemStackHooks.giveItem(player, stack.copy());
                }

                this.setGranted(player.getUuid(), true);
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

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            StarterKitData data = ModWorldData.STARTER_KIT.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        LifecycleEvent.SERVER_BEFORE_START.register(server -> {
            ModConfigs.STARTER_KIT = new StarterKitConfig().read();
        });
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private boolean granted;

        public Entry() {

        }

        public boolean isGranted() {
            return this.granted;
        }

        public void setGranted(boolean granted) {
            this.granted = granted;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.BOOLEAN.writeBits(this.granted, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            this.granted = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.BOOLEAN.writeNbt(this.granted).ifPresent(tag -> nbt.put("granted", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.granted = Adapters.BOOLEAN.readNbt(nbt.get("granted")).orElseThrow();
        }
    }

}
