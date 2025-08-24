package abeshutt.staracademy.block;

import abeshutt.staracademy.block.entity.BetterStructureBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BetterStructureBlock extends BlockWithEntity implements OperatorBlock {

    public static final MapCodec<BetterStructureBlock> CODEC = createCodec(BetterStructureBlock::new);
    public static final EnumProperty<StructureBlockMode> MODE = Properties.STRUCTURE_BLOCK_MODE;

    public BetterStructureBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(MODE, StructureBlockMode.LOAD));
    }

    public BetterStructureBlock() {
        this(Settings.create().mapColor(MapColor.LIGHT_GRAY).requiresTool()
                .strength(-1.0F, 3600000.0F).dropsNothing());
    }

    public MapCodec<BetterStructureBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BetterStructureBlockEntity(pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof BetterStructureBlockEntity structure) {
            if(world.isClient()) {
                structure.openScreen();
            }

            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            if (placer != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof BetterStructureBlockEntity) {
                    ((BetterStructureBlockEntity)blockEntity).setAuthor(placer);
                }
            }

        }
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world instanceof ServerWorld) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BetterStructureBlockEntity structureBlockBlockEntity) {
                boolean bl = world.isReceivingRedstonePower(pos);
                boolean bl2 = structureBlockBlockEntity.isPowered();
                if (bl && !bl2) {
                    structureBlockBlockEntity.setPowered(true);
                    this.doAction((ServerWorld)world, structureBlockBlockEntity);
                } else if (!bl && bl2) {
                    structureBlockBlockEntity.setPowered(false);
                }

            }
        }
    }

    private void doAction(ServerWorld world, BetterStructureBlockEntity blockEntity) {
        switch (blockEntity.getMode()) {
            case SAVE:
                blockEntity.saveStructure(false);
                break;
            case LOAD:
                blockEntity.loadAndPlaceStructure(world);
                break;
            case CORNER:
                blockEntity.unloadStructure();
            case DATA:
        }

    }

}
