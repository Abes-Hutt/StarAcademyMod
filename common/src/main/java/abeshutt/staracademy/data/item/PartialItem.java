package abeshutt.staracademy.data.item;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.Optional;

public class PartialItem implements ItemPlacement<PartialItem> {

    protected Identifier id;

    protected PartialItem(Identifier id) {
        this.id = id;
    }

    public static PartialItem empty() {
        return new PartialItem(null);
    }

    public static PartialItem of(Identifier id) {
        return new PartialItem(id);
    }

    public static PartialItem of(Item item) {
        return new PartialItem(Registries.ITEM.getId(item));
    }

    public static PartialItem of(ItemStack stack) {
        return new PartialItem(Registries.ITEM.getId(stack.getItem()));
    }

    @Override
    public boolean isSubsetOf(PartialItem other) {
        return this.id == null || this.id.equals(other.id);

    }

    @Override
    public boolean isSubsetOf(ItemStack stack) {
        return this.isSubsetOf(PartialItem.of(stack));
    }

    @Override
    public void fillInto(PartialItem other) {
        if(this.id == null) return;
        other.id = this.id;
    }

    @Override
    public Optional<ItemStack> generate(int count) {
        return this.asWhole().map(item -> new ItemStack(item, count));
    }

    @Override
    public boolean test(PartialItem item, PartialCompoundNbt nbt) {
        return this.isSubsetOf(item);
    }

    @Override
    public void validate(String path) {
        if(this.asWhole().isEmpty()) {
            StarAcademyMod.LOGGER.error("%s: Unregistered item <%s>".formatted(path, this.id));
        }
    }

    public Optional<Item> asWhole() {
        return Registries.ITEM.getOrEmpty(this.id);
    }

    @Override
    public PartialItem copy() {
        return new PartialItem(this.id);
    }

    @Override
    public String toString() {
        return this.id == null ? "" : this.id.toString();
    }

    public static class Adapter implements ISimpleAdapter<PartialItem, NbtElement, JsonElement> {
        @Override
        public Optional<NbtElement> writeNbt(PartialItem value) {
            return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(value.id);
        }

        @Override
        public Optional<PartialItem> readNbt(NbtElement nbt) {
            return nbt == null ? Optional.empty() : Adapters.IDENTIFIER.readNbt(nbt).map(PartialItem::of);
        }
    }

    public static Optional<PartialItem> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialItem parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialItem parse(StringReader reader) throws CommandSyntaxException {
        if(!reader.canRead() || !isCharValid(reader.peek())) {
            return PartialItem.empty();
        }

        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialItem.of(Identifier.of(string));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid item identifier '" + string + "' in stack '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
