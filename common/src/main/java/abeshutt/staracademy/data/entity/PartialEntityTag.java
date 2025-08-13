package abeshutt.staracademy.data.entity;

import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ModifiableWorld;

import java.util.Optional;

public class PartialEntityTag implements EntityPlacement<PartialEntityTag> {

    private Identifier id;
    private PartialCompoundNbt nbt;

    public PartialEntityTag(Identifier id, PartialCompoundNbt nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public static PartialEntityTag of(Identifier id, PartialCompoundNbt entity) {
        return new PartialEntityTag(id, entity);
    }

    @Override
    public boolean isSubsetOf(PartialEntityTag other) {
        return (this.id == null || this.id.equals(other.id))
            && this.nbt.isSubsetOf(other.nbt);
    }

    @Override
    public boolean isSubsetOf(Entity entity) {
        return false;
    }


    @Override
    public void fillInto(PartialEntityTag other) {
        if(this.id != null) {
            other.id = this.id;
        }

        this.nbt.fillInto(other.nbt);
    }

    @Override
    public void place(ModifiableWorld world) {

    }

    @Override
    public boolean test(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
        if(!this.nbt.isSubsetOf(nbt)) {
            return false;
        }

        Identifier id = this.nbt.asWhole()
            .filter(tag -> tag.contains("id", NbtElement.STRING_TYPE))
            .map(tag -> Identifier.tryParse(tag.getString("id")))
            .orElse(null);

        if(id == null || !Registries.ENTITY_TYPE.containsId(id)) {
            return false;
        }

        EntityType<?> type = Registries.ENTITY_TYPE.get(id);
        return Registries.ENTITY_TYPE.getEntry(type).streamTags()
                .anyMatch(tag -> tag.id().equals(this.id));
    }

    @Override
    public PartialEntityTag copy() {
        return new PartialEntityTag(this.id, this.nbt.copy());
    }

    @Override
    public String toString() {
        return (this.id != null ? "#" + this.id : "") + this.nbt.toString();
    }

    public static Optional<PartialEntityTag> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialEntityTag parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialEntityTag parse(StringReader reader) throws CommandSyntaxException {
        if(reader.peek() != '#') {
            throw new IllegalArgumentException("Invalid entity tag '" + reader.getString() + "' does not modeMinimum with #");
        }

        reader.skip();
        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialEntityTag.of(Identifier.of(string), PartialCompoundNbt.parse(reader));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid tag identifier '" + string + "' in block tag '" + reader.getString() + "'");
        }
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

}
