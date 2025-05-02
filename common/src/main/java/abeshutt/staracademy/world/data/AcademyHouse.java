package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.net.UpdateHousesS2CPacket;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class AcademyHouse implements ISerializable<NbtCompound, JsonObject> {

    private UUID uuid;
    private final Map<UUID, HousePlayer> players;
    private HousePokedexManager pokedex;

    private final Map<UUID, HousePlayer> changes;

    public AcademyHouse() {
        this.uuid = UUID.randomUUID();
        this.players = new LinkedHashMap<>();
        this.pokedex = new HousePokedexManager(this.uuid);

        this.changes = new LinkedHashMap<>();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Map<UUID, HousePlayer> getPlayers() {
        return this.players;
    }

    public HousePokedexManager getPokedex() {
        return this.pokedex;
    }

    public boolean onAdd(PlayerEntity player) {
        if(this.players.containsKey(player.getUuid())) {
            return false;
        }

        long time = ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
        HousePlayer housePlayer = new HousePlayer(player.getUuid(), time);
        this.players.put(housePlayer.getUuid(), housePlayer);
        this.changes.put(player.getUuid(), housePlayer);
        return true;
    }

    public boolean onRemove(PlayerEntity player) {
        if(!this.players.containsKey(player.getUuid())) {
            return false;
        }

        this.players.remove(player.getUuid());
        this.changes.put(player.getUuid(), null);
        return true;
    }

    public UpdateHousesS2CPacket.House getFullPacket() {
        UpdateHousesS2CPacket.House payload = new UpdateHousesS2CPacket.House();
        payload.players = new LinkedHashMap<>(this.players);
        payload.pokedex = new LinkedHashMap<>(this.pokedex.getSpeciesRecords());
        return payload;
    }

    public Optional<UpdateHousesS2CPacket.House> getChangesPacket() {
        if(this.changes.isEmpty() && this.pokedex.getChanges().isEmpty()) {
            return Optional.empty();
        }

        UpdateHousesS2CPacket.House payload = new UpdateHousesS2CPacket.House();
        payload.players = new LinkedHashMap<>(this.changes);
        payload.pokedex = new LinkedHashMap<>(this.pokedex.getChanges());
        return Optional.of(payload);
    }

    public void clearChanges() {
        this.changes.clear();
        this.pokedex.clearChanges();
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));

            NbtList players = new NbtList();

            for(HousePlayer entry : this.players.values()) {
                Adapters.HOUSE_PLAYER.writeNbt(entry).ifPresent(players::add);
            }

            nbt.put("players", players);

            Adapters.HOUSE_POKEDEX_MANAGER.writeNbt(this.pokedex).ifPresent(tag -> {
                nbt.put("pokedex", tag);
            });

            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();

        this.players.clear();
        NbtList players = nbt.getList("players", NbtElement.COMPOUND_TYPE);

        for(NbtElement player : players) {
            Adapters.HOUSE_PLAYER.readNbt(player).ifPresent(entry -> {
                this.players.put(entry.getUuid(), entry);
            });
        }

        this.pokedex = Adapters.HOUSE_POKEDEX_MANAGER.readNbt(nbt.get("pokedex")).orElse(new HousePokedexManager(this.uuid));
        this.pokedex.setUuid(this.uuid);
        this.pokedex.initialize();
    }

}
