package abeshutt.staracademy.data.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import abeshutt.staracademy.init.ModConfigs;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.Optional;

public class PartialItemGroup implements ItemPlacement<PartialItemGroup> {

    private Identifier id;
    private PartialCompoundNbt nbt;

    public PartialItemGroup(Identifier id, PartialCompoundNbt nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public static PartialItemGroup of(Identifier id, PartialCompoundNbt entity) {
        return new PartialItemGroup(id, entity);
    }

    public Identifier getId() {
        return this.id;
    }

    public PartialCompoundNbt getNbt() {
        return this.nbt;
    }

    @Override
    public boolean isSubsetOf(PartialItemGroup other) {
        return (this.id == null || this.id.equals(other.id))
            && this.nbt.isSubsetOf(other.nbt);
    }

    @Override
    public boolean isSubsetOf(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInto(PartialItemGroup other) {
        if(this.id != null) {
            other.id = this.id;
        }

        this.nbt.fillInto(other.nbt);
    }

    @Override
    public Optional<ItemStack> generate(int count) {
        return this.nbt.generate(count);
    }

    @Override
    public boolean test(PartialItem item, PartialCompoundNbt nbt) {
        return this.nbt.isSubsetOf(nbt)
            && ModConfigs.ITEM_GROUPS.isInGroup(this.id, item, nbt);
    }

    @Override
    public void validate(String path) {
        if(!ModConfigs.ITEM_GROUPS.hasGroup(this.id)) {
            StarAcademyMod.LOGGER.error("%s: Unregistered item group <%s>".formatted(path, this.id));
        }
    }

    @Override
    public PartialItemGroup copy() {
        return new PartialItemGroup(this.id, this.nbt.copy());
    }

    public static Optional<PartialItemGroup> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialItemGroup parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialItemGroup parse(StringReader reader) throws CommandSyntaxException {
        if(reader.peek() != '@') {
            throw new IllegalArgumentException("Invalid item group '" + reader.getString() + "' does not modeMinimum with @");
        }

        reader.skip();
        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialItemGroup.of(Identifier.of(string), PartialCompoundNbt.parse(reader));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid item identifier '" + string + "' in item group '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
