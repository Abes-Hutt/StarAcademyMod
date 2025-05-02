package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.net.UpdateHousesS2CPacket;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HouseData extends WorldData {

    public static final HouseData CLIENT = new HouseData();

    private final Map<UUID, AcademyHouse> houses;
    private final Map<UUID, AcademyHouse> changes;

    public HouseData() {
        this.houses = new LinkedHashMap<>();
        this.changes = new LinkedHashMap<>();
    }

    public Map<UUID, AcademyHouse> getHouses() {
        return this.houses;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    private void onJoin(ServerPlayerEntity player) {
        Map<UUID, UpdateHousesS2CPacket.House> changes = new LinkedHashMap<>();

        this.houses.forEach((uuid, house) -> {
            changes.put(uuid, house.getFullPacket());
        });

        NetworkManager.sendToPlayer(player, new UpdateHousesS2CPacket(null));
        NetworkManager.sendToPlayer(player, new UpdateHousesS2CPacket(changes));
    }

    private void onTick(MinecraftServer server) {
        Map<UUID, UpdateHousesS2CPacket.House> changes = new LinkedHashMap<>();

        this.houses.forEach((uuid, house) -> {
            house.getChangesPacket().ifPresent(packet -> changes.put(uuid, packet));
        });

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            NetworkManager.sendToPlayer(player, new UpdateHousesS2CPacket(changes));
        }

        this.houses.values().forEach(AcademyHouse::clearChanges);
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtList teams = new NbtList();

            for(AcademyHouse house : this.houses.values()) {
                Adapters.HOUSE.writeNbt(house).ifPresent(teams::add);
            }

            nbt.put("houses", teams);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList houses = nbt.getList("houses", NbtElement.COMPOUND_TYPE);

        for(NbtElement team : houses) {
            Adapters.HOUSE.readNbt(team).ifPresent(house -> {
                this.houses.put(house.getUuid(), house);
            });
        }
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            HouseData data = ModWorldData.HOUSE.getGlobal(player.getWorld());
            data.onJoin(player);
        });

        TickEvent.SERVER_POST.register(server -> {
            HouseData data = ModWorldData.HOUSE.getGlobal(server);
            data.onTick(server);
        });
    }

}
