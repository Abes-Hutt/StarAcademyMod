package abeshutt.staracademy.block;

import abeshutt.staracademy.block.entity.ShinyPokedollCollectorBlockEntity;
import abeshutt.staracademy.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShinyPokedollCollectorBlock extends BlockWithEntity {

    public static final MapCodec<ShinyPokedollCollectorBlock> CODEC = createCodec(ShinyPokedollCollectorBlock::new);

    public static final BooleanProperty LIT = Properties.LIT;

    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(1 / 16.0, 7 / 16.0, 1 / 16.0, 15 / 16.0, 11 / 16.0, 15 / 16.0),
            VoxelShapes.cuboid(5 / 16.0, 3 / 16.0, 1 / 16.0, 11 / 16.0, 7 / 16.0, 2 / 16.0),
            VoxelShapes.cuboid(14 / 16.0, 3 / 16.0, 5 / 16.0, 15 / 16.0, 7 / 16.0, 11 / 16.0),
            VoxelShapes.cuboid(1 / 16.0, 3 / 16.0, 5 / 16.0, 2 / 16.0, 7 / 16.0, 11 / 16.0),
            VoxelShapes.cuboid(5 / 16.0, 3 / 16.0, 14 / 16.0, 11 / 16.0, 7 / 16.0, 15 / 16.0),
            VoxelShapes.cuboid(5 / 16.0, 11 / 16.0, 5 / 16.0, 11 / 16.0, 13 / 16.0, 11 / 16.0),
            VoxelShapes.cuboid(1 / 16.0, 0 / 16.0, 1 / 16.0, 15 / 16.0, 3 / 16.0, 15 / 16.0),
            VoxelShapes.cuboid(2 / 16.0, 3 / 16.0, 2 / 16.0, 14 / 16.0, 7 / 16.0, 14 / 16.0)
    );

    public ShinyPokedollCollectorBlock() {
        super(Settings.copy(Blocks.ANVIL));
    }

    public ShinyPokedollCollectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShinyPokedollCollectorBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient() && type == ModBlocks.Entities.SHINY_POKEDOLL_COLLECTOR.get()) {
            return (world1, pos, state1, blockEntity) -> ((ShinyPokedollCollectorBlockEntity) blockEntity).tick(state1);
        }
        return super.getTicker(world, state, type);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        world.setBlockState(pos, state.cycle(LIT), 2);
        return ActionResult.SUCCESS;
    }
}
