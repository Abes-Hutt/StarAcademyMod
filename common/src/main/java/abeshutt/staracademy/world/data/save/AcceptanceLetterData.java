package abeshutt.staracademy.world.data.save;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class AcceptanceLetterData extends WorldData {

    private final Map<UUID, Entry> entries;

    public AcceptanceLetterData() {
        this.entries = new LinkedHashMap<>();
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
        private boolean open;
        private boolean accepted;
        private final Set<String> options;

        public Entry() {
            this.options = new LinkedHashSet<>();
        }

        public Entry(Collection<String> options) {
            this.options = new LinkedHashSet<>(options);
        }

        @Override
        public void writeBits(BitBuffer buffer) {
            Adapters.BOOLEAN.writeBits(this.open, buffer);
            Adapters.BOOLEAN.writeBits(this.accepted, buffer);
            Adapters.INT_SEGMENTED_3.writeBits(this.options.size(), buffer);

            for(String option : this.options) {
                Adapters.UTF_8.writeBits(option, buffer);
            }
        }

        @Override
        public void readBits(BitBuffer buffer) {
            this.open = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
            this.accepted = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
            this.options.clear();

            for(int i = 0; i < size; i++) {
                this.options.add(Adapters.UTF_8.readBits(buffer).orElseThrow());
            }
        }

        @Override
        public Optional<NbtCompound> writeNbt() {
            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.BOOLEAN.writeNbt(this.open).ifPresent(tag -> nbt.put("open", tag));
                Adapters.BOOLEAN.writeNbt(this.accepted).ifPresent(tag -> nbt.put("accepted", tag));

                NbtList options = new NbtList();

                for(String option : this.options) {
                    Adapters.UTF_8.writeNbt(option).ifPresent(options::add);
                }

                nbt.put("options", options);
                return nbt;
            });
        }

        @Override
        public void readNbt(NbtCompound nbt) {
            this.open = Adapters.BOOLEAN.readNbt(nbt.get("open")).orElseThrow();
            this.accepted = Adapters.BOOLEAN.readNbt(nbt.get("accepted")).orElseThrow();
            this.options.clear();

            if(nbt.get("options") instanceof NbtList options) {
                for(NbtElement option : options) {
                    Adapters.UTF_8.readNbt(option).ifPresent(this.options::add);
                }
            }
        }
    }

}
