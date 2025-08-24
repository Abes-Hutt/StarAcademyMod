package abeshutt.staracademy.block;

import abeshutt.staracademy.block.entity.HousePokedexBlockEntity;
import abeshutt.staracademy.screen.HousePokedexScreen;
import abeshutt.staracademy.world.data.HouseData;
import com.cobblemon.mod.common.client.pokedex.PokedexType;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HousePokedexBlock extends BlockWithEntity {

    public static final MapCodec<EnchantingTableBlock> CODEC = createCodec(EnchantingTableBlock::new);
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public HousePokedexBlock() {
        super(Settings.create()
                .mapColor(MapColor.RED)
                .requiresTool()
                .luminance((state) -> 7)
                .strength(-1.0F, 3600000.0F)
                .dropsNothing().sounds(BlockSoundGroup.METAL));
    }

    @Override
    public MapCodec<EnchantingTableBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        PlayerEntity player = world.getClosestPlayer((double)pos.getX() + 0.5,
                (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);

        if(player == null) {
            return;
        }

        for(int i = 0; i < 10; i++) {
            if(random.nextInt(16) == 0) {
                world.addParticle(ParticleTypes.ENCHANT,
                        (double)pos.getX() + 0.5, (double)pos.getY() + 2.0, (double)pos.getZ() + 0.5,
                        random.nextFloat() - 0.5,
                        random.nextFloat() - 0.5,
                        random.nextFloat() - 0.5);
            }
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HousePokedexBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if(blockEntity instanceof HousePokedexBlockEntity pokedex) {
                pokedex.tick();
            }
        };
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(world.isClient) {
            HouseData.CLIENT.getFor(player.getUuid()).ifPresent(house -> {
                HousePokedexScreen.open(house.getPokedex(), PokedexType.BLACK, null, null);
            });

            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.CONSUME;
        }
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

}
