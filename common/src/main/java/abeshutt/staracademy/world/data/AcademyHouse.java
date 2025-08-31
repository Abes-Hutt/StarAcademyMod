package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.net.UpdateHousesS2CPacket;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AcademyHouse implements ISerializable<NbtCompound, JsonObject> {

    private String id;
    private String name;
    private int color;
    private final Map<UUID, HousePlayer> players;
    private HousePokedexManager pokedex;

    private boolean propertiesDirty;
    private final Map<UUID, HousePlayer> playerChanges;

    public AcademyHouse() {
        this(null);
    }

    public AcademyHouse(String id) {
        this.id = id;
        this.players = new LinkedHashMap<>();
        this.pokedex = new HousePokedexManager();
        this.name = "Unknown";
        this.color = 0xFFFFFF;

        this.propertiesDirty = false;
        this.playerChanges = new LinkedHashMap<>();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean setName(String name) {
        if(!this.name.equals(this.name = name)) {
            this.propertiesDirty = true;
            return true;
        }

        return false;
    }

    public int getColor() {
        return this.color;
    }

    public boolean setColor(int color) {
        if(this.color != (this.color = color)) {
            this.propertiesDirty = true;
            return true;
        }

        return false;
    }

    public Map<UUID, HousePlayer> getPlayers() {
        return this.players;
    }

    public HousePokedexManager getPokedex() {
        return this.pokedex;
    }

    public boolean addPlayer(UUID uuid) {
        if(this.players.containsKey(uuid)) {
            return false;
        }

        long time = ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
        HousePlayer housePlayer = new HousePlayer(uuid, time);
        this.players.put(housePlayer.getUuid(), housePlayer);
        this.playerChanges.put(uuid, housePlayer);
        return true;
    }

    public boolean removePlayer(UUID uuid) {
        if(!this.players.containsKey(uuid)) {
            return false;
        }

        this.players.remove(uuid);
        this.playerChanges.put(uuid, null);
        return true;
    }

    public UpdateHousesS2CPacket.House getFullPacket() {
        UpdateHousesS2CPacket.House payload = new UpdateHousesS2CPacket.House();
        payload.name = this.name;
        payload.color = this.color;
        payload.players = new LinkedHashMap<>(this.players);
        payload.pokedex = new LinkedHashMap<>(this.pokedex.getSpeciesRecords());
        return payload;
    }

    public Optional<UpdateHousesS2CPacket.House> getChangesPacket() {
        if(!this.propertiesDirty && this.playerChanges.isEmpty() && this.pokedex.getChanges().isEmpty()) {
            return Optional.empty();
        }

        UpdateHousesS2CPacket.House payload = new UpdateHousesS2CPacket.House();
        payload.name = this.name;
        payload.color = this.color;
        payload.players = new LinkedHashMap<>(this.playerChanges);
        payload.pokedex = new LinkedHashMap<>(this.pokedex.getChanges());
        return Optional.of(payload);
    }

    public void clearChanges() {
        this.propertiesDirty = false;
        this.playerChanges.clear();
        this.pokedex.clearChanges();
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UTF_8.writeNbt(this.id).ifPresent(tag -> nbt.put("id", tag));
            Adapters.UTF_8.writeNbt(this.name).ifPresent(tag -> nbt.put("name", tag));
            Adapters.INT.writeNbt(this.color).ifPresent(tag -> nbt.put("color", tag));

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
        this.id = Adapters.UTF_8.readNbt(nbt.get("id")).orElseThrow();
        this.name = Adapters.UTF_8.readNbt(nbt.get("name")).orElseThrow();
        this.color = Adapters.INT.readNbt(nbt.get("color")).orElseThrow();

        this.players.clear();
        NbtList players = nbt.getList("players", NbtElement.COMPOUND_TYPE);

        for(NbtElement player : players) {
            Adapters.HOUSE_PLAYER.readNbt(player).ifPresent(entry -> {
                this.players.put(entry.getUuid(), entry);
            });
        }

        this.pokedex = Adapters.HOUSE_POKEDEX_MANAGER.readNbt(nbt.get("pokedex")).orElseGet(HousePokedexManager::new);
        this.pokedex.initialize();
    }

}
