package abeshutt.staracademy.block;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.block.entity.SafariPortalBlockEntity;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.util.ProxyEntity;
import abeshutt.staracademy.world.data.SafariData;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SafariPortalBlock extends Block implements BlockEntityProvider {

    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public SafariPortalBlock() {
        super(AbstractBlock.Settings.create()
                .noCollision()
                .strength(-1.0F)
                .sounds(BlockSoundGroup.GLASS)
                .luminance(state -> 11)
                .pistonBehavior(PistonBehavior.BLOCK));
        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch(state.get(AXIS)) {
            case Z -> Z_SHAPE;
            default -> X_SHAPE;
        };
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = state.get(AXIS);
        boolean bl = axis2 != axis && axis.isHorizontal();
        return !bl && !neighborState.isOf(this) && !new SafariPortal(world, pos, axis2).wasAlreadyValid()
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(!(world instanceof ServerWorld)) {
            return;
        }

        ProxyEntity proxy = ProxyEntity.of(entity).orElseThrow();

        if(entity instanceof ServerPlayerEntity player && entity.canUsePortals() && !proxy.hasSafariPortalCooldown()
                && !proxy.isInSafariPortal() && VoxelShapes.matchesAnywhere(
                VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())),
                state.getOutlineShape(world, pos),
                BooleanBiFunction.AND)) {
            proxy.setInSafariPortal(true);

            SafariData data = ModWorldData.SAFARI.getGlobal(world);
            SafariData.Entry entry = data.get(player.getUuid()).orElse(null);

            if(world.getRegistryKey() == StarAcademyMod.SAFARI) {
                data.leaveSafari(player);
            } else {
                if(data.getTimeLeft() <= 0 || data.isPaused()) {
                    player.sendMessage(Text.empty()
                            .append(Text.literal("The Safari is currently unavailable.")
                                    .formatted(Formatting.RED)), true);
                    return;
                } else if(entry != null && entry.getTimeLeft() <= 0) {
                    player.sendMessage(Text.empty()
                            .append(Text.literal("You have no time left in the Safari.")
                                    .formatted(Formatting.RED)), true);
                    return;
                }

                data.joinSafari(player);
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for(int i = 0; i < 4; i++) {
            double posX = (double)pos.getX() + random.nextDouble();
            double posY = (double)pos.getY() + random.nextDouble();
            double posZ = (double)pos.getZ() + random.nextDouble();
            double velocityX = ((double)random.nextFloat() - 0.5) * 0.5;
            double velocityY = ((double)random.nextFloat() - 0.5) * 0.5;
            double velocityZ = ((double)random.nextFloat() - 0.5) * 0.5;
            int direction = random.nextInt(2) * 2 - 1;

            if(!world.getBlockState(pos.west()).isOf(this) && !world.getBlockState(pos.east()).isOf(this)) {
                posX = (double)pos.getX() + 0.5 + 0.25 * (double)direction;
                velocityX = random.nextFloat() * 2.0F * (float)direction;
            } else {
                posZ = (double)pos.getZ() + 0.5 + 0.25 * (double)direction;
                velocityZ = random.nextFloat() * 2.0F * (float)direction;
            }

            world.addParticle(ParticleTypes.ASH, posX, posY, posZ, velocityX, velocityY, velocityZ);
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch(rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch(state.get(AXIS)) {
                case Z -> state.with(AXIS, Direction.Axis.X);
                case X -> state.with(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SafariPortalBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if(blockEntity instanceof SafariPortalBlockEntity portal) {
                portal.tick();
            }
        };
    }

}
