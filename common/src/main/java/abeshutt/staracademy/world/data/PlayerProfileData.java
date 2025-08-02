package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdatePlayerProfileS2CPacket;
import abeshutt.staracademy.util.ProxyGameProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProfileData extends WorldData {

    public static final PlayerProfileData CLIENT = new PlayerProfileData();

    private final Map<UUID, GameProfile> profiles;
    private final Map<UUID, CompletableFuture<GameProfile>> futures;

    public PlayerProfileData() {
        this.profiles = new ConcurrentHashMap<>();
        this.futures = new LinkedHashMap<>();
    }

    public Map<UUID, GameProfile> getProfiles() {
        return this.profiles;
    }

    public Optional<GameProfile> getProfile(UUID uuid) {
        return Optional.ofNullable(this.profiles.get(uuid));
    }

    public synchronized CompletableFuture<GameProfile> getProfileAsync(MinecraftServer server, UUID uuid) {
        if(this.profiles.containsKey(uuid)) {
            return CompletableFuture.completedFuture(this.profiles.get(uuid));
        }

        CompletableFuture<GameProfile> future = this.futures.get(uuid);

        if(future == null) {
            future = CompletableFuture.supplyAsync(() -> {
                ProfileResult result = server.getSessionService().fetchProfile(uuid, true);
                GameProfile profile = result == null ? new GameProfile(uuid, "Unknown") : result.profile();
                this.profiles.put(uuid, profile);
                this.futures.remove(uuid);
                this.markDirty();
                NetworkManager.sendToPlayers(server.getPlayerManager().getPlayerList(),
                        new UpdatePlayerProfileS2CPacket(uuid, profile));
                return profile;
            });

            this.futures.put(uuid, future);
        }

        return future;
    }

    private void onJoin(ServerPlayerEntity player) {
        this.profiles.remove(player.getUuid());
        this.getProfileAsync(player.getServer(), player.getUuid());
        NetworkManager.sendToPlayer(player, new UpdatePlayerProfileS2CPacket(this.profiles));
    }

    private void onTick(MinecraftServer server) {
        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            GameProfile profile = this.profiles.get(player.getUuid());

            if(profile != null && profile.getName().equals("Unknown")) {
                ProxyGameProfile.of(profile).ifPresent(proxy -> {
                    proxy.setName(player.getGameProfile().getName());
                    this.markDirty();
                    NetworkManager.sendToPlayers(server.getPlayerManager().getPlayerList(),
                            new UpdatePlayerProfileS2CPacket(player.getUuid(), profile));
                });
            }
        }
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            this.profiles.forEach((uuid, profile) -> {
                Adapters.GAME_PROFILE.writeNbt(profile).ifPresent(tag -> nbt.put(uuid.toString(), tag));
            });

            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.profiles.clear();

        for(String key : nbt.getKeys()) {
            Adapters.GAME_PROFILE.readNbt(nbt.get(key)).ifPresent(profile -> {
                this.profiles.put(UUID.fromString(key), profile);
            });
        }
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            PlayerProfileData data = ModWorldData.PLAYER_PROFILE.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            PlayerProfileData data = ModWorldData.PLAYER_PROFILE.getGlobal(server);
            data.onTick(server);
        });
    }

}
