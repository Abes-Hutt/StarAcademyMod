package abeshutt.staracademy.data.tile;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

public class PartialBlock implements TilePlacement<PartialBlock> {

    protected Identifier id;

    protected PartialBlock(Identifier id) {
        this.id = id;
    }

    public static PartialBlock empty() {
        return new PartialBlock(null);
    }

    public static PartialBlock of(Identifier id) {
        return new PartialBlock(id);
    }

    public static PartialBlock of(Block block) {
        return new PartialBlock(Registries.BLOCK.getId(block));
    }

    public static PartialBlock of(BlockState state) {
        return new PartialBlock(Registries.BLOCK.getId(state.getBlock()));
    }

    @Override
    public boolean isSubsetOf(PartialBlock other) {
        return this.id == null || this.id.equals(other.id);

    }

    @Override
    public boolean isSubsetOf(BlockView world, BlockPos pos) {
        return this.isSubsetOf(PartialBlock.of(world.getBlockState(pos).getBlock()));
    }

    @Override
    public void fillInto(PartialBlock other) {
        if(this.id == null) return;
        other.id = this.id;
    }

    @Override
    public void place(WorldAccess world, BlockPos pos, int flags) {
        this.asWhole().ifPresent(block -> {
            BlockState oldState = world.getBlockState(pos);
            BlockState newState = block.getDefaultState();

            for(Property property : oldState.getProperties()) {
                if(!newState.contains(property)) continue;
                newState = newState.with(property, oldState.get(property));
            }

            world.setBlockState(pos, newState, flags);
        });
    }

    @Override
    public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
        return this.isSubsetOf(state.getBlock());
    }

    public Optional<Block> asWhole() {
        if(!Registries.BLOCK.containsId(this.id)) {
            return Optional.empty();
        }

        return Registries.BLOCK.getOrEmpty(this.id);
    }

    @Override
    public PartialBlock copy() {
        return new PartialBlock(this.id);
    }

    @Override
    public String toString() {
        return this.id == null ? "" : this.id.toString();
    }

    public static class Adapter implements ISimpleAdapter<PartialBlock, NbtElement, JsonElement> {
        @Override
        public Optional<NbtElement> writeNbt(PartialBlock value) {
            return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(value.id);
        }

        @Override
        public Optional<PartialBlock> readNbt(NbtElement nbt) {
            return nbt == null ? Optional.empty() : Adapters.IDENTIFIER.readNbt(nbt).map(PartialBlock::of);
        }
    }

    public static Optional<PartialBlock> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialBlock parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialBlock parse(StringReader reader) throws CommandSyntaxException {
        if(!reader.canRead() || !isCharValid(reader.peek())) {
            return PartialBlock.empty();
        }

        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialBlock.of(Identifier.of(string));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid block identifier '" + string + "' in tile '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
