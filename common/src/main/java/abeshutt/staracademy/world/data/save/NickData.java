package abeshutt.staracademy.world.data.save;

import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateNickS2CPacket;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class NickData extends WorldData {

    public static final NickData CLIENT = new NickData();

    private final Map<UUID, String> entries;
    private final Set<UUID> changes;

    public NickData() {
        this.entries = new LinkedHashMap<>();
        this.changes = new HashSet<>();
    }

    public Map<UUID, String> getEntries() {
        return this.entries;
    }

    public Optional<String> get(UUID uuid) {
        return Optional.ofNullable(this.entries.get(uuid));
    }

    public void set(UUID uuid, String nick) {
        this.changes.add(uuid);

        if(nick == null) {
            this.entries.remove(uuid);
        } else {
            this.entries.put(uuid, nick);
        }

        this.markDirty();
    }

    public void onTick(MinecraftServer server) {
        Map<UUID, String> entries = new HashMap<>();

        for(UUID change : this.changes) {
            entries.put(change, this.entries.get(change));
        }

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            NetworkManager.sendToPlayer(player, new UpdateNickS2CPacket(entries));
        }
    }

    public void onJoin(ServerPlayerEntity player) {
        NetworkManager.sendToPlayer(player, new UpdateNickS2CPacket(null));
        NetworkManager.sendToPlayer(player, new UpdateNickS2CPacket(this.entries));
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            NickData data = ModWorldData.NICK.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            NickData data = ModWorldData.NICK.getGlobal(server);
            data.onTick(server);
        });
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtCompound entries = new NbtCompound();

            this.entries.forEach((uuid, nick) -> {
                entries.putString(uuid.toString(), nick);
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
            this.entries.put(UUID.fromString(key), entries.getString(key));
        }
    }

}
