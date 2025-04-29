package abeshutt.staracademy.data.biome;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class PartialBiome implements BiomePlacement<PartialBiome> {

    protected Identifier id;

    protected PartialBiome(Identifier id) {
        this.id = id;
    }

    public static PartialBiome empty() {
        return new PartialBiome(null);
    }

    public static PartialBiome of(Identifier id) {
        return new PartialBiome(id);
    }

    public static PartialBiome of(Block block) {
        return new PartialBiome(Registries.BLOCK.getId(block));
    }

    public static PartialBiome of(BlockState state) {
        return new PartialBiome(Registries.BLOCK.getId(state.getBlock()));
    }

    @Override
    public boolean isSubsetOf(PartialBiome other) {
        return this.id == null || this.id.equals(other.id);

    }

    @Override
    public boolean isSubsetOf(BlockView world, BlockPos pos) {
        return this.isSubsetOf(PartialBiome.of(world.getBlockState(pos).getBlock()));
    }

    @Override
    public void fillInto(PartialBiome other) {
        if(this.id == null) return;
        other.id = this.id;
    }

    @Override
    public boolean test(RegistryEntry<Biome> biome) {
        return this.isSubsetOf(PartialBiome.of(
            biome.getKey().map(RegistryKey::getValue).orElse(null)
        ));
    }

    @Override
    public PartialBiome copy() {
        return new PartialBiome(this.id);
    }

    @Override
    public String toString() {
        return this.id == null ? "" : this.id.toString();
    }

    public static class Adapter implements ISimpleAdapter<PartialBiome, NbtElement, JsonElement> {
        @Override
        public Optional<NbtElement> writeNbt(PartialBiome value) {
            return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(value.id);
        }

        @Override
        public Optional<PartialBiome> readNbt(NbtElement nbt) {
            return nbt == null ? Optional.empty() : Adapters.IDENTIFIER.readNbt(nbt).map(PartialBiome::of);
        }
    }

    public static Optional<PartialBiome> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialBiome parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialBiome parse(StringReader reader) throws CommandSyntaxException {
        if(!reader.canRead() || !isCharValid(reader.peek())) {
            return PartialBiome.empty();
        }

        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialBiome.of(Identifier.of(string));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid biome identifier '" + string + "' in '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
