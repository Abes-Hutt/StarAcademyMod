package abeshutt.staracademy.data.entity;

import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import abeshutt.staracademy.init.ModConfigs;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ModifiableWorld;

import java.util.Objects;
import java.util.Optional;

public class PartialEntityGroup implements EntityPlacement<PartialEntityGroup> {

    private Identifier id;
    private PartialCompoundNbt nbt;

    public PartialEntityGroup(Identifier id, PartialCompoundNbt nbt) {
        this.id = id;
        this.nbt = nbt;
    }

    public static PartialEntityGroup of(Identifier id, PartialCompoundNbt entity) {
        return new PartialEntityGroup(id, entity);
    }

    @Override
    public boolean isSubsetOf(PartialEntityGroup other) {
        return (this.id == null || this.id.equals(other.id))
            && this.nbt.isSubsetOf(other.nbt);
    }

    @Override
    public boolean isSubsetOf(Entity entity) {
        return false;
    }

    @Override
    public void fillInto(PartialEntityGroup other) {
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
        return this.nbt.isSubsetOf(nbt) && ModConfigs.ENTITY_GROUPS.isInGroup(this.id, pos, blockPos, nbt);
    }

    @Override
    public PartialEntityGroup copy() {
        return new PartialEntityGroup(this.id, this.nbt.copy());
    }

    @Override
    public String toString() {
        return (this.id != null ? "@" + this.id : "") + this.nbt.toString();
    }

    public static Optional<PartialEntityGroup> parse(String string, boolean logErrors) {
        try {
            return Optional.of(parse(new StringReader(string)));
        } catch(CommandSyntaxException | IllegalArgumentException e) {
            if(logErrors) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public static PartialEntityGroup parse(String string) throws CommandSyntaxException {
        return parse(new StringReader(string));
    }

    public static PartialEntityGroup parse(StringReader reader) throws CommandSyntaxException {
        if(reader.peek() != '@') {
            throw new IllegalArgumentException("Invalid entity group '" + reader.getString() + "' does not modeMinimum with @");
        }

        reader.skip();
        int cursor = reader.getCursor();

        while(reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try {
            return PartialEntityGroup.of(Identifier.of(string), PartialCompoundNbt.parse(reader));
        } catch(InvalidIdentifierException e) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid group identifier '" + string + "' in entity group '" + reader.getString() + "'");
        }
    }

    public Identifier getId() {
        return id;
    }

    protected static boolean isCharValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartialEntityGroup that = (PartialEntityGroup) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
