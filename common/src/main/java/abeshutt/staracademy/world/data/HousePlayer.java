package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;
import java.util.UUID;

public class HousePlayer implements ISerializable<NbtCompound, JsonObject> {

    private UUID uuid;
    private long joinTime;

    public HousePlayer() {

    }

    public HousePlayer(UUID uuid, long joinTime) {
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
    public void writeBits(BitBuffer buffer) {
        Adapters.UUID.writeBits(this.uuid, buffer);
        Adapters.LONG.writeBits(this.joinTime, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.uuid = Adapters.UUID.readBits(buffer).orElseThrow();
        this.joinTime = Adapters.LONG.readBits(buffer).orElseThrow();
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
