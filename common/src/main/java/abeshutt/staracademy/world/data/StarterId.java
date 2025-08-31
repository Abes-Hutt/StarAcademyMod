package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.serializable.ISerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class StarterId implements ISerializable<NbtCompound, JsonObject> {

    private final String category;
    private final int index;

    public StarterId(String category, int index) {
        this.category = category;
        this.index = index;
    }

    public String getCategory() {
        return category;
    }

    public int getIndex() {
        return index;
    }

    public Optional<StarterPokemon> resolve() {
        List<StarterPokemon> starters = PokemonStarterData.CLIENT.getStarters();

        for(StarterPokemon starter : starters) {
            if(starter.getId().equals(this)) {
                return Optional.of(starter);
            }
        }

        return Optional.empty();
    }

    @Override
    public int hashCode() {
        return this.category.hashCode() + this.index * 31;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof StarterId id) {
            return this.index == id.index && this.category.equals(id.category);
        }

        return false;
    }

    public static class Adapter implements ISimpleAdapter<StarterId, NbtElement, JsonElement> {
        private final boolean nullable;

        public Adapter(boolean nullable) {
            this.nullable = nullable;
        }

        public boolean isNullable() {
            return this.nullable;
        }

        public Adapter asNullable() {
            return new Adapter(true);
        }

        @Override
        public void writeBits(StarterId value, BitBuffer buffer) {
            if(this.nullable) {
                Adapters.BOOLEAN.writeBits(value == null, buffer);
            }

            if(value != null) {
                Adapters.UTF_8.writeBits(value.getCategory(), buffer);
                Adapters.INT_SEGMENTED_3.writeBits(value.getIndex(), buffer);
            }
        }

        @Override
        public Optional<StarterId> readBits(BitBuffer buffer) {
            if(this.nullable && Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                return Optional.empty();
            }

            return Optional.of(new StarterId(
                    Adapters.UTF_8.readBits(buffer).orElseThrow(),
                    Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow()
            ));
        }

        @Override
        public Optional<NbtElement> writeNbt(StarterId value) {
            if(value == null) {
                return Optional.empty();
            }

            return Optional.of(new NbtCompound()).map(nbt -> {
                Adapters.UTF_8.writeNbt(value.getCategory()).ifPresent(tag -> nbt.put("category", tag));
                Adapters.INT_SEGMENTED_3.writeNbt(value.getIndex()).ifPresent(tag -> nbt.put("index", tag));
                return nbt;
            });
        }

        @Override
        public Optional<StarterId> readNbt(NbtElement nbt) {
            if(!(nbt instanceof NbtCompound compound)) {
                return Optional.empty();
            }

            return Optional.of(new StarterId(
                    Adapters.UTF_8.readNbt(compound.get("category")).orElseThrow(),
                    Adapters.INT_SEGMENTED_3.readNbt(compound.get("index")).orElseThrow()
            ));
        }
    }

}
