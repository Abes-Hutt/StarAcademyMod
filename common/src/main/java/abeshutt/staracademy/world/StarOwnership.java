package abeshutt.staracademy.world;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StarOwnership implements ISerializable<NbtCompound, JsonObject> {

    private final List<Entry> entries;

    public StarOwnership() {
        this.entries = new ArrayList<>();
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public static StarOwnership.Entry ofNow(UUID uuid) {
        long time = Instant.now().toEpochMilli();
        return new StarOwnership.Entry(uuid, time);
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.INT_SEGMENTED_3.writeBits(this.entries.size(), buffer);

        for(Entry entry : this.entries) {
            entry.writeBits(buffer);
        }
    }

    @Override
    public void readBits(BitBuffer buffer) {
        int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
        this.entries.clear();

        for(int i = 0; i < size; i++) {
           Entry entry = new Entry(null, 0L);
           entry.readBits(buffer);
           this.entries.add(entry);
        }
    }

    @Override
    public Optional<NbtCompound> writeNbt() {
        return Optional.of(new NbtCompound()).map(nbt -> {
            NbtList entries = new NbtList();

            for(Entry entry : this.entries) {
               entry.writeNbt().ifPresent(entries::add);
            }

            nbt.put("entries", entries);
            return nbt;
        });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.entries.clear();

        if(nbt.get("entries") instanceof NbtList entries) {
            for(NbtElement element : entries) {
               if(element instanceof NbtCompound compound) {
                   Entry entry = new Entry(null, 0L);
                   entry.readNbt(compound);
                   this.entries.add(entry);
               }
            }
        }
    }

    public static class Entry implements ISerializable<NbtCompound, JsonObject> {
        private UUID uuid;
        private long time;

        public Entry(UUID uuid, long time) {
            this.uuid = uuid;
            this.time = time;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public long getTime() {
            return this.time;
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.UUID.writeBits(this.uuid, buffer);
            Adapters.LONG.writeBits(this.time, buffer);
        }

        @Override
        public void readBits(BitBuffer buffer) {
            this.uuid = Adapters.UUID.readBits(buffer).orElseThrow();
            this.time = Adapters.LONG.readBits(buffer).orElseThrow();
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.UUID.writeNbt(this.uuid).ifPresent(tag -> nbt.put("uuid", tag));
                Adapters.LONG.writeNbt(this.time).ifPresent(tag -> nbt.put("time", tag));
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElseThrow();
            this.time = Adapters.LONG.readNbt(nbt.get("time")).orElseThrow();
        }
    }

}
