package abeshutt.staracademy.world;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import abeshutt.staracademy.world.data.StarterId;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class StarterEntry implements ISerializable<NbtCompound, JsonObject> {

    private StarterId pick;
    private StarterId granted;
    private boolean available;
    private boolean changed;

    public StarterEntry() {
        this.available = false;
        this.changed = true;
    }

    public StarterId getPick() {
        return this.pick;
    }

    public void setPick(StarterId pick) {
        this.pick = pick;
        this.setChanged(true);
    }

    public StarterId getGranted() {
        return this.granted;
    }

    public void setGranted(StarterId granted) {
        this.granted = granted;
        this.setChanged(true);
    }

    public boolean isAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
        this.setChanged(true);
    }

    public void onCompleteRound() {
        this.pick = null;
        this.setChanged(true);
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.STARTER_ID.asNullable().writeBits(this.pick, buffer);
        Adapters.STARTER_ID.asNullable().writeBits(this.granted, buffer);
        Adapters.BOOLEAN.writeBits(this.available, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.pick = Adapters.STARTER_ID.asNullable().readBits(buffer).orElse(null);
        this.granted = Adapters.STARTER_ID.asNullable().readBits(buffer).orElse(null);
        this.available = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            Adapters.STARTER_ID.writeNbt(this.pick).ifPresent(tag -> nbt.put("pick", tag));
            Adapters.STARTER_ID.writeNbt(this.granted).ifPresent(tag -> nbt.put("granted", tag));
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.pick = Adapters.STARTER_ID.readNbt(nbt.get("pick")).orElse(null);
        this.granted = Adapters.STARTER_ID.readNbt(nbt.get("granted")).orElse(null);
    }

}
