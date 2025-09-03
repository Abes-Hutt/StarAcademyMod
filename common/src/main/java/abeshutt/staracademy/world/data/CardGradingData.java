package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateCardGradingS2CPacket;
import com.google.gson.JsonObject;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class CardGradingData extends WorldData {

    public static final CardGradingData CLIENT = new CardGradingData();

    private final Map<UUID, Entry> entries;
    private final Set<UUID> changes;

    public CardGradingData() {
        this.entries = new LinkedHashMap<>();
        this.changes = new HashSet<>();
    }

    public Map<UUID, Entry> getEntries() {
        return this.entries;
    }

    public boolean has(UUID uuid) {
        return this.entries.containsKey(uuid);
    }

    public void add(UUID uuid, ItemStack stack) {
        this.entries.put(uuid, new Entry(
                ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
                stack));
        this.changes.add(uuid);
        this.markDirty();
    }

    public void remove(UUID uuid) {
        this.entries.remove(uuid);
        this.changes.add(uuid);
        this.markDirty();
    }

    public long getTimeLeft(UUID uuid) {
        Entry entry = this.entries.get(uuid);
        if(entry == null) return 0;
        long now = ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
        return entry.time + ModConfigs.NPC.getGradingTimeMillis() - now;
    }

    public ItemStack getStack(UUID uuid) {
        Entry entry = this.entries.get(uuid);
        if(entry == null) return ItemStack.EMPTY;
        return entry.stack;
    }

    private void onJoin(ServerPlayerEntity player) {
        NetworkManager.sendToPlayer(player, new UpdateCardGradingS2CPacket(null));
        Entry entry = this.entries.get(player.getUuid());

        if(entry != null) {
            NetworkManager.sendToPlayer(player, new UpdateCardGradingS2CPacket(player.getUuid(), entry));
        }
    }

    public void onTick(MinecraftServer server) {
        for(UUID change : this.changes) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(change);
            if(player == null) continue;
            NetworkManager.sendToPlayer(player, new UpdateCardGradingS2CPacket(player.getUuid(),
                    this.entries.get(change)));
        }

        this.changes.clear();
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
        private long time;
        private ItemStack stack;

        public Entry() {

        }

        public Entry(long time, ItemStack stack) {
            this.time = time;
            this.stack = stack;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.LONG.writeBits(this.time, buffer);
            Adapters.ITEM_STACK.writeBits(this.stack, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            this.time = Adapters.LONG.readBits(buffer).orElseThrow();
            this.stack = Adapters.ITEM_STACK.readBits(buffer).orElseThrow();
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.LONG.writeNbt(this.time).ifPresent(tag -> nbt.put("time", tag));
                Adapters.ITEM_STACK.writeNbt(this.stack).ifPresent(tag -> nbt.put("stack", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.time = Adapters.LONG.readNbt(nbt).orElseThrow();
            this.stack = Adapters.ITEM_STACK.readNbt(nbt).orElseThrow();
        }
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            CardGradingData data = ModWorldData.CARD_GRADING.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            CardGradingData data = ModWorldData.CARD_GRADING.getGlobal(server);
            data.onTick(server);
        });
    }

}
