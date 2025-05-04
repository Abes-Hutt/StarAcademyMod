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

    public static final EnumProperty<StructureBlockMode> MODE = Properties.STRUCTURE_BLOCK_MODE;

    public BetterStructureBlock() {
        super(Settings.create().mapColor(MapColor.LIGHT_GRAY).requiresTool()
                .strength(-1.0F, 3600000.0F).dropsNothing());
        this.setDefaultState(this.stateManager.getDefaultState().with(MODE, StructureBlockMode.LOAD));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BetterStructureBlockEntity(pos, state);
    }


    @Override
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

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(!world.isClient && placer != null && world.getBlockEntity(pos) instanceof BetterStructureBlockEntity structure) {
            structure.setAuthor(placer);
        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if(world instanceof ServerWorld && world.getBlockEntity(pos) instanceof BetterStructureBlockEntity structure) {
            boolean isPowered = world.isReceivingRedstonePower(pos);
            boolean wasPowered = structure.isPowered();

            if(isPowered && !wasPowered) {
                structure.setPowered(true);
                this.doAction((ServerWorld)world, structure);
            } else if(!isPowered && wasPowered) {
                structure.setPowered(false);
            }
        }
    }

    private void doAction(ServerWorld world, BetterStructureBlockEntity structure) {
        switch(structure.getMode()) {
            case SAVE -> structure.saveStructure(false);
            case LOAD -> structure.loadStructure(world, false);
            case CORNER -> structure.unloadStructure();
        }
    }

}
