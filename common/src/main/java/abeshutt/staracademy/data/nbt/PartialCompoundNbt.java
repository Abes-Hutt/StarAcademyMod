package abeshutt.staracademy.data.nbt;

import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.adapter.ISimpleAdapter;
import abeshutt.staracademy.data.bit.BitBuffer;
import abeshutt.staracademy.data.entity.EntityPlacement;
import abeshutt.staracademy.data.item.ItemPlacement;
import abeshutt.staracademy.data.item.PartialItem;
import abeshutt.staracademy.data.tile.PartialBlockState;
import abeshutt.staracademy.data.tile.TilePlacement;
import abeshutt.staracademy.util.ItemUtils;
import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

public class PartialCompoundNbt implements TilePlacement<PartialCompoundNbt>, EntityPlacement<PartialCompoundNbt>, ItemPlacement<PartialCompoundNbt> {

	private NbtCompound nbt;

	protected PartialCompoundNbt(NbtCompound nbt) {
		this.nbt = nbt;
	}

	public static PartialCompoundNbt empty() {
		return new PartialCompoundNbt(new NbtCompound());
	}

	public static PartialCompoundNbt of(NbtCompound nbt) {
		return new PartialCompoundNbt(nbt);
	}

	public static PartialCompoundNbt of(Entity entity) {
		if(entity == null) return new PartialCompoundNbt(null);
		NbtCompound nbt = new NbtCompound();
		nbt.putString("id", EntityType.getId(entity.getType()).toString());
		return new PartialCompoundNbt(entity.writeNbt(nbt));
	}

	public static PartialCompoundNbt of(BlockEntity blockEntity) {
		if(blockEntity == null) return new PartialCompoundNbt(null);
		return new PartialCompoundNbt(blockEntity.createNbtWithId(blockEntity.getWorld().getRegistryManager()));
	}

	public static PartialCompoundNbt at(BlockView world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity == null) return new PartialCompoundNbt(null);
		return new PartialCompoundNbt(blockEntity.createNbtWithId(blockEntity.getWorld().getRegistryManager()));
	}

	public static PartialCompoundNbt of(ItemStack stack) {
		return new PartialCompoundNbt(ItemUtils.getNbt(stack));
	}

	@Override
	public boolean isSubsetOf(PartialCompoundNbt other) {
		if(this.nbt == null) {
			return true;
		} else if(other.nbt == null || this.nbt.getSize() > other.nbt.getSize()) {
			return false;
		}

		for(String key : this.nbt.getKeys()) {
			NbtElement nbt1 = this.nbt.get(key);
			NbtElement nbt2 = other.nbt.get(key);
			if(nbt1 == null) continue;
			if(nbt2 == null || nbt1.getType() != nbt2.getType()) return false;

			if(nbt1.getType() == NbtElement.COMPOUND_TYPE) {
				if(!PartialCompoundNbt.of((NbtCompound)nbt1).isSubsetOf(PartialCompoundNbt.of((NbtCompound)nbt2))) {
					return false;
				}
			} else if(nbt1 instanceof AbstractNbtList<?>) {
				if(!PartialListNbt.of((AbstractNbtList<?>)nbt1).isSubsetOf(PartialListNbt.of((AbstractNbtList<?>)nbt2))) {
					return false;
				}
			} else if(!nbt1.equals(nbt2)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isSubsetOf(BlockView world, BlockPos pos) {
		if(this.nbt == null) {
			return true;
		}

		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity != null && this.isSubsetOf(PartialCompoundNbt.of(blockEntity.createNbtWithId(
				blockEntity.getWorld().getRegistryManager())));
	}

	@Override
	public boolean isSubsetOf(Entity entity) {
		return this.isSubsetOf(PartialCompoundNbt.of(entity));
	}

	@Override
	public boolean isSubsetOf(ItemStack stack) {
		return this.isSubsetOf(PartialCompoundNbt.of(stack));
	}

	@Override
	public void fillInto(PartialCompoundNbt other) {
		if(this.nbt == null) {
			return;
		}

		if(other.nbt == null) {
			other.nbt = new NbtCompound();
		}

		for(String key : this.nbt.getKeys()) {
			NbtElement e = this.nbt.get(key);
			if(e == null) continue;
			e = e.copy();

			if(e.getType() == NbtElement.COMPOUND_TYPE) {
				if(!other.nbt.contains(key)) {
					other.nbt.put(key, e);
				} else {
					PartialCompoundNbt.of((NbtCompound)e).fillInto(PartialCompoundNbt.of(other.nbt.getCompound(key)));
				}
			} else {
				other.nbt.put(key, e);
			}
		}
	}

	@Override
	public void place(WorldAccess world, BlockPos pos, int flags) {
		if(this.nbt == null) return;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity == null) return;
		blockEntity.read(this.nbt, world.getRegistryManager());
	}

	@Override
	public void place(ModifiableWorld world) {

	}

	@Override
	public Optional<ItemStack> generate(int count) {
		return Optional.empty();
	}

	@Override
	public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
		return this.isSubsetOf(nbt);
	}

	@Override
	public boolean test(Vec3d pos, BlockPos blockPos, PartialCompoundNbt nbt) {
		return this.isSubsetOf(nbt);
	}

	@Override
	public boolean test(PartialItem item, PartialCompoundNbt nbt) {
		return this.isSubsetOf(nbt);
	}

	public Optional<NbtCompound> asWhole() {
		return Optional.ofNullable(this.nbt);
	}

	@Override
	public PartialCompoundNbt copy() {
		return new PartialCompoundNbt(this.nbt == null ? null : this.nbt.copy());
	}

	@Override
	public String toString() {
		return this.nbt == null ? "" : this.nbt.toString();
	}

	public static class Adapter implements ISimpleAdapter<PartialCompoundNbt, NbtElement, JsonElement> {
		@Override
		public void writeBits(PartialCompoundNbt value, BitBuffer buffer) {
			buffer.writeBoolean(value != null);

			if(value != null) {
				Adapters.COMPOUND_NBT.asNullable().writeBits(value.nbt, buffer);
			}
		}

		@Override
		public Optional<PartialCompoundNbt> readBits(BitBuffer buffer) {
			if(buffer.readBoolean()) {
				return Adapters.COMPOUND_NBT.asNullable().readBits(buffer).map(PartialCompoundNbt::of);
			}

			return Optional.empty();
		}

		@Override
		public Optional<NbtElement> writeNbt(PartialCompoundNbt value) {
			return value == null ? Optional.empty() : Adapters.COMPOUND_NBT.writeNbt(value.nbt);
		}

		@Override
		public Optional<PartialCompoundNbt> readNbt(NbtElement nbt) {
			return nbt == null ? Optional.empty() : Adapters.COMPOUND_NBT.readNbt(nbt).map(PartialCompoundNbt::of);
		}

		@Override
		public Optional<JsonElement> writeJson(PartialCompoundNbt value) {
			return value == null ? Optional.empty() : Adapters.COMPOUND_NBT.writeJson(value.nbt);
		}

		@Override
		public Optional<PartialCompoundNbt> readJson(JsonElement json) {
			return json == null ? Optional.empty() : Adapters.COMPOUND_NBT.readJson(json).map(PartialCompoundNbt::of);
		}
	}

	public static Optional<PartialCompoundNbt> parse(String string, boolean logErrors) {
		try {
			return Optional.of(parse(new StringReader(string)));
		} catch(CommandSyntaxException | IllegalArgumentException e) {
			if(logErrors) {
				e.printStackTrace();
			}
		}

		return Optional.empty();
	}

	public static PartialCompoundNbt parse(String string) throws CommandSyntaxException {
		return parse(new StringReader(string));
	}

	public static PartialCompoundNbt parse(StringReader reader) throws CommandSyntaxException {
		if(reader.canRead() && reader.peek() == '{') {
			String string = reader.getString().substring(reader.getCursor());
			int index = string.lastIndexOf('}');

			if(index < 0) {
				throw new IllegalArgumentException("Unclosed nbt in tile '" + reader.getString() + "'");
			}

			return PartialCompoundNbt.of(new StringNbtReader(new StringReader(string.substring(0, index + 1))).parseCompound());
		}

		return PartialCompoundNbt.of((NbtCompound)null);
	}

}
