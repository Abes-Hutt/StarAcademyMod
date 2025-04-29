package abeshutt.staracademy.data.entity;

import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ModifiableWorld;

import java.util.Optional;

public class PartialEntity implements EntityPlacement<PartialEntity> {

	private Vec3d pos;
	private BlockPos blockPos;
	private PartialCompoundNbt nbt;

	protected PartialEntity(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
		this.pos = pos;
		this.blockPos = blockPos;
		this.nbt = nbt;
	}

	public static PartialEntity of(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
		return new PartialEntity(pos, blockPos, nbt);
	}

	public static PartialEntity of(Vec3d pos, BlockPos blockPos, Identifier id, PartialCompoundNbt nbt) {
		if(id != null) {
			NbtCompound tag = nbt.asWhole().orElse(new NbtCompound());
			tag.putString("id", id.toString());
			return new PartialEntity(pos, blockPos, PartialCompoundNbt.of(tag));
		}

		return new PartialEntity(pos, blockPos, nbt);
	}

	public static PartialEntity of(Entity entity) {
		return new PartialEntity(entity.getPos(), entity.getBlockPos(), PartialCompoundNbt.of(entity));
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public PartialCompoundNbt getNbt() {
		return this.nbt;
	}

	public void setPos(Vec3d pos) {
		this.pos = pos;
	}

	public void setBlockPos(BlockPos blockPos) {
		this.blockPos = blockPos;
	}

	public void setNbt(PartialCompoundNbt nbt) {
		this.nbt = nbt;
	}

	@Override
	public boolean isSubsetOf(PartialEntity other) {
		return this.nbt.isSubsetOf(other.nbt);
	}

	@Override
	public boolean isSubsetOf(Entity entity) {
		return this.isSubsetOf(PartialEntity.of(entity));
	}

	@Override
	public void fillInto(PartialEntity other) {
		if(this.pos != null) {
			other.pos = this.pos;
		}

		if(this.blockPos != null) {
			other.blockPos = this.blockPos.toImmutable();
		}

		this.nbt.fillInto(other.nbt);
	}

	@Override
	public void place(ModifiableWorld world) {

	}

	@Override
	public boolean test(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
		return this.nbt.isSubsetOf(nbt);
	}

	@Override
	public PartialEntity copy() {
		return new PartialEntity(this.pos, this.blockPos.toImmutable(), this.nbt.copy());
	}

	@Override
	public String toString() {
		return this.nbt.toString();
	}

	public static Optional<PartialEntity> parse(String string, boolean logErrors) {
		try {
			return Optional.of(parse(new StringReader(string)));
		} catch(CommandSyntaxException | IllegalArgumentException e) {
			if(logErrors) {
				e.printStackTrace();
			}
		}

		return Optional.empty();
	}

	public static PartialEntity parse(String string) throws CommandSyntaxException {
		return parse(new StringReader(string));
	}

	public static PartialEntity parse(StringReader reader) throws CommandSyntaxException {
		if(!reader.canRead() || !isCharValid(reader.peek())) {
			return PartialEntity.of(null, null, null, PartialCompoundNbt.parse(reader));
		}

		int cursor = reader.getCursor();

		while(reader.canRead() && isCharValid(reader.peek())) {
			reader.skip();
		}

		String string = reader.getString().substring(cursor, reader.getCursor());

		try {
			return PartialEntity.of(null, null, string.isEmpty() ? null : Identifier.of(string), PartialCompoundNbt.parse(reader));
		} catch(InvalidIdentifierException e) {
			reader.setCursor(cursor);
			throw new IllegalArgumentException("Invalid entity identifier '" + string + "' in entity '" + reader.getString() + "'");
		}
	}

	protected static boolean isCharValid(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
	}

}
