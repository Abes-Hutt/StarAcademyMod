package abeshutt.staracademy.compat.enhancedcelestials.block.entity;

import abeshutt.staracademy.compat.enhancedcelestials.EnhancedCelestialsCompat;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class LunarForecastHologramBlockEntity extends BlockEntity {

    public LunarForecastHologramBlockEntity(BlockPos pos, BlockState state) {
        super(EnhancedCelestialsCompat.LUNAR_FORECAST_HOLOGRAM_BLOCK_ENTITY.get(), pos, state);
    }
}
