package abeshutt.staracademy.data.biome;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class PartialBiomeTag implements BiomePlacement<PartialBiomeTag> {

    private Identifier id;

    public PartialBiomeTag(Identifier id) {
        this.id = id;
    }

    public static PartialBiomeTag of(Identifier id) {
        return new PartialBiomeTag(id);
    }

    @Override
    public boolean isSubsetOf(PartialBiomeTag other) {
        return this.id == null || this.id.equals(other.id);
    }

    @Override
    public boolean isSubsetOf(BlockView world, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInto(PartialBiomeTag other) {
        if(this.id != null) {
            other.id = this.id;
        }
    }

    @Override
    public boolean test(RegistryEntry<Biome> biome) {
        return biome.streamTags().anyMatch(tag -> tag.id().equals(this.id));
    }

    @Override
    public PartialBiomeTag copy() {
        return new PartialBiomeTag(this.id);
    }

    @Override
    public String toString() {
        return this.id != null ? "#" + this.id : "";
    }

    public static Optional<PartialBiomeTag> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialBiomeTag parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialBiomeTag parse(StringReader reader) throws CommandSyntaxException {
        if(reader.peek() != '#') {
            throw new IllegalArgumentException("Invalid block tag '" + reader.getString() + "' does not modeMinimum with #");
        }

        reader.skip();
        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialBiomeTag.of(Identifier.of(string));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid tag identifier '" + string + "' in biome tag '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
