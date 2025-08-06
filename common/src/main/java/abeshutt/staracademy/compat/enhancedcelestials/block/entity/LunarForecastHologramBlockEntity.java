package abeshutt.staracademy.compat.enhancedcelestials.block.entity;

import abeshutt.staracademy.compat.enhancedcelestials.EnhancedCelestialsCompat;
import abeshutt.staracademy.compat.enhancedcelestials.block.LunarForecastHologramBlock;
import abeshutt.staracademy.init.ModConfigs;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials.lunarevent.LunarEventInstance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class LunarForecastHologramBlockEntity extends BlockEntity {
    private int idx;
    private int ticks;

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

    public static void serverTick(World world, BlockPos pos, BlockState state, LunarForecastHologramBlockEntity hologramBlockEntity) {
        if (world.isClient || state.get(LunarForecastHologramBlock.LIT)) {
            return;
        }
        hologramBlockEntity.ticks++;

        if (hologramBlockEntity.ticks % ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getHologramSwitchTime() == 0) {
            hologramBlockEntity.next();
        }
    }


    public void next() {
        if (world == null) return;
        if (world.isClient) return;

        EnhancedCelestials.lunarForecastWorldData(world).ifPresent(enhancedCelestialsLunarForecastWorldData -> {
            int size = enhancedCelestialsLunarForecastWorldData.getForecast().size();
            int nextIdx = (this.idx + 1) % (size - 1);

            List<LunarEventInstance> forecast = enhancedCelestialsLunarForecastWorldData.getForecast();
            if (forecast.isEmpty()) {
                this.idx = -1;
                sync();
                return;
            }

            if (forecast.getFirst().getDaysUntil(enhancedCelestialsLunarForecastWorldData.getCurrentDay()) >= ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getForecastDayView()) {
                this.idx = -1;
                sync();
                return;
            }

            LunarEventInstance lunarEventInstance = forecast.get(nextIdx);
            if (lunarEventInstance.getDaysUntil(enhancedCelestialsLunarForecastWorldData.getCurrentDay()) <= ModConfigs.ENHANCED_CELESTIALS_COBBLEMON_CONFIG.getForecastDayView()) {
                this.idx = nextIdx;
                sync();
            } else {
                this.idx = 0;
                sync();
            }
        });
    }

    private void sync() {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(getPos());
        }
    }

    public int getIdx() {
        return idx;
    }
}
