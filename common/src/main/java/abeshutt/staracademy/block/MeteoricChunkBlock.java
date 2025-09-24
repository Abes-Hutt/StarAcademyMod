package abeshutt.staracademy.block;

import abeshutt.staracademy.compat.enhancedcelestials.block.LunarForecastHologramBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MeteoricChunkBlock extends BlockWithEntity {

    public static final MapCodec<MeteoricChunkBlock> CODEC = createCodec(MeteoricChunkBlock::new);

    public MeteoricChunkBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World _world, BlockState _state, BlockEntityType<T> type) {
        return (world, pos, state, blockEntity) -> {
            Random random = world.random;

            if(random.nextFloat() < 0.11F) {
                for(int i = 0; i < random.nextInt(2) + 2; i++) {
                    CampfireBlock.spawnSmokeParticle(world, pos, false, false);
                }
            }
        };
    }

}
