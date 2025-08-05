package abeshutt.staracademy.compat.enhancedcelestials.block.entity;

import abeshutt.staracademy.compat.enhancedcelestials.EnhancedCelestialsCompat;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LunarForecastHologramBlockEntity extends BlockEntity {
    private int idx;

    public LunarForecastHologramBlockEntity(BlockPos pos, BlockState state) {
        super(EnhancedCelestialsCompat.LUNAR_FORECAST_HOLOGRAM_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("idx", this.idx);

    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.idx = nbt.getInt("idx");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public void rightClick() {
        if (world == null) return;
        if (world.isClient) return;

        EnhancedCelestials.lunarForecastWorldData(world).ifPresent(enhancedCelestialsLunarForecastWorldData -> {
            int size = enhancedCelestialsLunarForecastWorldData.getForecast().size();
            this.idx = (this.idx + 1) % (size - 1);
            if(world instanceof ServerWorld serverWorld) {
                serverWorld.getChunkManager().markForUpdate(getPos());
            }
        });
    }

    public int getIdx() {
        return idx;
    }
}
