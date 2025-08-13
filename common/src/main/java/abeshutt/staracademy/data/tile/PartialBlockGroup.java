package abeshutt.staracademy.data.tile;

import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import abeshutt.staracademy.init.ModConfigs;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

public class PartialBlockGroup implements TilePlacement<PartialBlockGroup> {

    private Identifier id;
    private PartialBlockProperties properties;
    private PartialCompoundNbt entity;

    public PartialBlockGroup(Identifier id, PartialBlockProperties properties, PartialCompoundNbt entity) {
        this.id = id;
        this.properties = properties;
        this.entity = entity;
    }

    public static PartialBlockGroup of(Identifier id, PartialBlockProperties properties, PartialCompoundNbt entity) {
        return new PartialBlockGroup(id, properties, entity);
    }

    @Override
    public boolean isSubsetOf(PartialBlockGroup other) {
        return (this.id == null || this.id.equals(other.id))
            && this.properties.isSubsetOf(other.properties)
            && this.entity.isSubsetOf(other.entity);
    }

    @Override
    public boolean isSubsetOf(BlockView world, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInto(PartialBlockGroup other) {
        if(this.id != null) {
            other.id = this.id;
        }

        this.properties.fillInto(other.properties);
        this.entity.fillInto(other.entity);
    }

    @Override
    public void place(WorldAccess world, BlockPos pos, int flags) {
        this.properties.place(world, pos, flags);
        this.entity.place(world, pos, flags);
    }

    @Override
    public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
        return this.properties.isSubsetOf(state.getProperties())
            && this.entity.isSubsetOf(nbt)
            && ModConfigs.TILE_GROUPS.isInGroup(this.id, state, nbt);
    }

    @Override
    public PartialBlockGroup copy() {
        return new PartialBlockGroup(this.id, this.properties.copy(), this.entity.copy());
    }

    @Override
    public String toString() {
        return (this.id != null ? "@" + this.id : "") + this.properties.toString() + this.entity.toString();
    }

    public static Optional<PartialBlockGroup> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialBlockGroup parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialBlockGroup parse(StringReader reader) throws CommandSyntaxException {
        if(reader.peek() != '@') {
            throw new IllegalArgumentException("Invalid block group '" + reader.getString() + "' does not modeMinimum with @");
        }

        reader.skip();
        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialBlockGroup.of(Identifier.of(string), PartialBlockProperties.parse(reader), PartialCompoundNbt.parse(reader));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid group identifier '" + string + "' in block group '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
