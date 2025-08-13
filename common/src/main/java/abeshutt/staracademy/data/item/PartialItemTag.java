package abeshutt.staracademy.data.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.Optional;

public class PartialItemTag implements ItemPlacement<PartialItemTag> {

    private Identifier id;
    private PartialCompoundNbt nbt;

    public PartialItemTag(Identifier id, PartialCompoundNbt nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public Identifier getId() {
        return this.id;
    }

    public PartialCompoundNbt getNbt() {
        return this.nbt;
    }

    public static PartialItemTag of(Identifier id, PartialCompoundNbt entity) {
        return new PartialItemTag(id, entity);
    }

    @Override
    public boolean isSubsetOf(PartialItemTag other) {
        return (this.id == null || this.id.equals(other.id))
            && this.nbt.isSubsetOf(other.nbt);
    }

    @Override
    public boolean isSubsetOf(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInto(PartialItemTag other) {
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
            && item.asWhole().map(other -> Registries.ITEM.getEntry(other).streamTags()
            .anyMatch(tag -> tag.id().equals(this.id))).orElse(false);
    }

    @Override
    public void validate(String path) {
        boolean used = false;

        for(Item item : Registries.ITEM) {
            if(Registries.ITEM.getEntry(item).streamTags().anyMatch(key -> this.getId().equals(key.id()))) {
                used = true;
                break;
            }
        }

        if(!used) {
            StarAcademyMod.LOGGER.error("%s: Unused item tag <%s>".formatted(path, this.id));
        }
    }

    @Override
    public PartialItemTag copy() {
        return new PartialItemTag(this.id, this.nbt.copy());
    }

    @Override
    public String toString() {
        return "#" + (this.id == null ? "" : this.id) + this.nbt.toString();
    }

    public static Optional<PartialItemTag> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialItemTag parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialItemTag parse(StringReader reader) throws CommandSyntaxException {
        if(reader.peek() != '#') {
            throw new IllegalArgumentException("Invalid item tag '" + reader.getString() + "' does not modeMinimum with #");
        }

        reader.skip();
        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialItemTag.of(Identifier.of(string), PartialCompoundNbt.parse(reader));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid tag identifier '" + string + "' in item tag '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
