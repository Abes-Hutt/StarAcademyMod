package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class AcademyTeam implements ISerializable<NbtCompound, JsonObject> {

    private UUID uuid;
    private final Map<UUID, PlayerEntry> players;

    public AcademyTeam() {
        this.players = new LinkedHashMap<>();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean onJoin(PlayerEntity player) {
        if(this.players.containsKey(player.getUuid())) {
            return false;
        }

        long time = ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli();
        this.players.put(player.getUuid(), new PlayerEntry(player.getUuid(), time));
        return true;
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));

            NbtList players = new NbtList();

            for(PlayerEntry entry : this.players.values()) {
                PlayerEntry.ADAPTER.writeNbt(entry).ifPresent(players::add);
            }

            nbt.put("players", players);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();

        this.players.clear();
        NbtList players = nbt.getList("players", NbtElement.COMPOUND_TYPE);

        for(int i = 0; i < players.size(); i++) {
            PlayerEntry.ADAPTER.readNbt(players.getCompound(i)).ifPresent(entry -> {
                this.players.put(entry.getUuid(), entry);
            });
        }
    }

    private static class PlayerEntry implements ISerializable<NbtCompound, JsonObject> {
        private static final ISimpleAdapter<PlayerEntry, NbtCompound, JsonObject> ADAPTER = Adapters
                .of(PlayerEntry::new, false);

        private UUID uuid;
        private long joinTime;

        private PlayerEntry() {

        }

        public PlayerEntry(UUID uuid, long joinTime) {
            this.uuid = uuid;
            this.joinTime = joinTime;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public long getJoinTime() {
            return this.joinTime;
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
                Adapters.LONG.writeNbt(this.joinTime).ifPresent(tag -> nbt.put("joinTime", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();
            this.joinTime = Adapters.LONG.readNbt(nbt.get("joinTime")).orElseThrow();
        }
    }

}
