package abeshutt.staracademy.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public abstract class BaseBlockEntity extends BlockEntity {

    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type);

    public abstract void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, UpdateType type);

    @Override
    public final void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        this.writeNbt(nbt, registries, UpdateType.SERVER);
    }

    @Override
    public final void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.readNbt(nbt, registries, UpdateType.SERVER);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        this.writeNbt(nbt, registries, UpdateType.INITIAL_PACKET);
        return nbt;
    }

    @Override
    public final BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, (entity, registries) -> {
            NbtCompound nbt = new NbtCompound();

            if(entity instanceof BaseBlockEntity baseEntity) {
                baseEntity.writeNbt(nbt, registries, UpdateType.UPDATE_PACKET);
            } else {
                throw new IllegalStateException("BlockEntity is not an instance of BaseBlockEntity");
            }

            return nbt;
        });
    }

    public void sendUpdatesToClient() {
        this.markDirty();

        if(this.getWorld() != null) {
            this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
            this.getWorld().updateNeighbors(this.getPos(), this.getCachedState().getBlock());
        }
    }

    public enum UpdateType {
        SERVER, INITIAL_PACKET, UPDATE_PACKET
    }

}
