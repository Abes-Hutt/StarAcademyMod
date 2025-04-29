package abeshutt.staracademy.data.tile;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.nbt.PartialCompoundNbt;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

public class PartialTile implements TilePlacement<PartialTile> {

	public static PartialTile ERROR = PartialTile.of(PartialBlockState.parse("dailyshop:error", true).orElseThrow(), PartialCompoundNbt.empty(), null);

	protected PartialBlockState state;
	protected PartialCompoundNbt entity;
	protected BlockPos pos;

	protected PartialTile(PartialBlockState state, PartialCompoundNbt entity, BlockPos pos) {
		this.state = state;
		this.entity = entity;
		this.pos = pos;
	}

	public static PartialTile of(PartialBlockState state, PartialCompoundNbt entity, BlockPos pos) {
		return new PartialTile(state, entity, pos);
	}

	public static PartialTile of(PartialBlockState state, PartialCompoundNbt entity) {
		return new PartialTile(state, entity, null);
	}

	public static PartialTile at(BlockView world, BlockPos pos) {
		return new PartialTile(PartialBlockState.at(world, pos), PartialCompoundNbt.at(world, pos), pos);
	}

	public static PartialTile of(BlockState state) {
		return new PartialTile(PartialBlockState.of(state), PartialCompoundNbt.empty(), null);
	}

	public PartialBlockState getState() {
		return this.state;
	}

	public PartialCompoundNbt getEntity() {
		return this.entity;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public PartialTile setState(PartialBlockState state) {
		this.state = state;
		return this;
	}

	public PartialTile setEntity(PartialCompoundNbt entity) {
		this.entity = entity;
		return this;
	}

	public PartialTile setPos(BlockPos pos) {
		this.pos = pos;
		return this;
	}

	@Override
	public boolean isSubsetOf(PartialTile other) {
		if(!this.state.isSubsetOf(other.state)) return false;
		if(!this.entity.isSubsetOf(other.entity)) return false;
		return true;
	}

	@Override
	public boolean isSubsetOf(BlockView world, BlockPos pos) {
		if(!this.state.isSubsetOf(world, pos)) return false;
		if(!this.entity.isSubsetOf(world, pos)) return false;
		return true;
	}

	@Override
	public void fillInto(PartialTile other) {
		this.state.fillInto(other.state);
		this.entity.fillInto(other.entity);

		if(this.pos != null) {
			other.pos = this.pos.toImmutable();
		}
	}

	@Override
	public void place(WorldAccess world, BlockPos pos, int flags) {
		if(pos != null) {
			this.state.place(world, pos, flags);
			this.entity.place(world, pos, flags);
		} else if(this.pos != null) {
			this.state.place(world, this.pos, flags);
			this.entity.place(world, this.pos, flags);
		}
	}

	@Override
	public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
		return this.isSubsetOf(PartialTile.of(state, nbt, null));
	}

	@Override
	public PartialTile copy() {
		return new PartialTile(this.state.copy(), this.entity.copy(), this.pos == null ? null : this.pos.toImmutable());
	}

	@Override
	public String toString() {
		return this.state.toString() + this.entity.toString();
	}

	public static class Adapter implements ISimpleAdapter<PartialTile, NbtElement, JsonElement> {
		@Override
		public Optional<NbtElement> writeNbt(PartialTile value) {
			if(value == null) {
				return Optional.empty();
			}

			NbtCompound nbt = new NbtCompound();

			if(value.pos != null) {
				NbtList posNBT = new NbtList();
				posNBT.add(NbtInt.of(value.pos.getX()));
				posNBT.add(NbtInt.of(value.pos.getY()));
				posNBT.add(NbtInt.of(value.pos.getZ()));
				nbt.put("pos", posNBT);
			}

			Adapters.PARTIAL_BLOCK_STATE.writeNbt(value.state).ifPresent(tag -> nbt.put("state", tag));
			Adapters.PARTIAL_NBT.writeNbt(value.entity).ifPresent(tag -> nbt.put("nbt", tag));
			return Optional.of(nbt);
		}

		@Override
		public Optional<PartialTile> readNbt(NbtElement nbt) {
			if(nbt instanceof NbtCompound compound) {
				PartialBlockState state = Adapters.PARTIAL_BLOCK_STATE.readNbt(compound.get("state")).orElseThrow();
				PartialCompoundNbt entity = Adapters.PARTIAL_NBT.readNbt(compound.get("nbt")).orElseGet(PartialCompoundNbt::empty);
				BlockPos pos = null;

				if(compound.contains("pos", NbtElement.LIST_TYPE)) {
					NbtList posNBT = compound.getList("pos", NbtElement.INT_TYPE);
					pos = new BlockPos(posNBT.getInt(0), posNBT.getInt(1), posNBT.getInt(2));
				}

				return Optional.of(PartialTile.of(state, entity, pos));
			}

			return Optional.empty();
		}
	}

	public static Optional<PartialTile> parse(String string, boolean logErrors) {
		try {
			return Optional.of(parse(new StringReader(string)));
		} catch(CommandSyntaxException | IllegalArgumentException e) {
			if(logErrors) {
				e.printStackTrace();
			}
		}

		return Optional.empty();
	}

	public static PartialTile parse(String string) throws CommandSyntaxException {
		return parse(new StringReader(string));
	}

	public static PartialTile parse(StringReader reader) throws CommandSyntaxException {
		return PartialTile.of(PartialBlockState.parse(reader), PartialCompoundNbt.parse(reader), null);
	}

}
