package abeshutt.staracademy.block;

import abeshutt.staracademy.block.entity.MeteoricChunkBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MeteoricChunkBlock extends Block implements BlockEntityProvider {

    public MeteoricChunkBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MeteoricChunkBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World _world, BlockState _state, BlockEntityType<T> type) {
        return (world, pos, state, blockEntity) -> {
            if(blockEntity instanceof MeteoricChunkBlockEntity chunk) {
                chunk.tick();
            }
        };
    }

}
