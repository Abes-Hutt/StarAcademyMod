package abeshutt.staracademy.data.biome;

import abeshutt.staracademy.init.ModConfigs;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class PartialBiomeGroup implements BiomePlacement<PartialBiomeGroup> {

    private Identifier id;

    public PartialBiomeGroup(Identifier id) {
        this.id = id;
    }

    public static PartialBiomeGroup of(Identifier id) {
        return new PartialBiomeGroup(id);
    }

    @Override
    public boolean isSubsetOf(PartialBiomeGroup other) {
        return this.id == null || this.id.equals(other.id);
    }

    @Override
    public boolean isSubsetOf(BlockView world, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInto(PartialBiomeGroup other) {
        if(this.id != null) {
            other.id = this.id;
        }
    }

    @Override
    public boolean test(RegistryEntry<Biome> biome) {
        return ModConfigs.BIOME_GROUPS.isInGroup(this.id, biome);
    }

    @Override
    public PartialBiomeGroup copy() {
        return new PartialBiomeGroup(this.id);
    }

    @Override
    public String toString() {
        return this.id != null ? "@" + this.id : "";
    }

    public static Optional<PartialBiomeGroup> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialBiomeGroup parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialBiomeGroup parse(StringReader reader) throws CommandSyntaxException {
        if(reader.peek() != '@') {
            throw new IllegalArgumentException("Invalid biome group '" + reader.getString() + "' does not modeMinimum with @");
        }

        reader.skip();
        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialBiomeGroup.of(Identifier.of(string));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid group identifier '" + string + "' in biome group '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
