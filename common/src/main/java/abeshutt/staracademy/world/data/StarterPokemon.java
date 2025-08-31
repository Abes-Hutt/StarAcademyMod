package abeshutt.staracademy.world.data;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonElement;
import net.minecraft.nbt.NbtElement;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StarterPokemon {

    private final StarterId id;
    private final Species species;
    private final Set<String> aspects;

    public StarterPokemon(StarterId id, Species species, Set<String> aspects) {
        this.id = id;
        this.species = species;
        this.aspects = aspects;
    }

    public StarterId getId() {
        return this.id;
    }

    public Species getSpecies() {
        return this.species;
    }

    public Set<String> getAspects() {
        return this.aspects;
    }

    public RenderablePokemon asRenderable() {
        return new RenderablePokemon(this.species, this.aspects);
    }

    public static class Adapter implements ISimpleAdapter<StarterPokemon, NbtElement, JsonElement> {
        private final boolean nullable;

        public Adapter(boolean nullable) {
            this.nullable = nullable;
        }

        public boolean isNullable() {
            return this.nullable;
        }

        public StarterId.Adapter asNullable() {
            return new StarterId.Adapter(true);
        }

        @Override
        public void writeBits(StarterPokemon value, BitBuffer buffer) {
            if(this.nullable) {
                Adapters.BOOLEAN.writeBits(value == null, buffer);
            }

            if(value != null) {
                Adapters.STARTER_ID.writeBits(value.getId(), buffer);
                Adapters.IDENTIFIER.writeBits(value.getSpecies().getResourceIdentifier(), buffer);
                Adapters.INT_SEGMENTED_3.writeBits(value.getAspects().size(), buffer);

                for(String aspect : value.getAspects()) {
                   Adapters.UTF_8.writeBits(aspect, buffer);
                }
            }
        }

        @Override
        public Optional<StarterPokemon> readBits(BitBuffer buffer) {
            if(this.nullable && Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
                return Optional.empty();
            }

            StarterId id = Adapters.STARTER_ID.readBits(buffer).orElseThrow();
            Species species = PokemonSpecies.INSTANCE.getByIdentifier(Adapters.IDENTIFIER.readBits(buffer).orElseThrow());
            Set<String> aspects = new HashSet<>();
            int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

            for(int i = 0; i < size; i++) {
                aspects.add(Adapters.UTF_8.readBits(buffer).orElseThrow());
            }

            return Optional.of(new StarterPokemon(id, species, aspects));
        }
    }

}
