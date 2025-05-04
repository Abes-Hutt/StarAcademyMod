package abeshutt.staracademy.block;

import abeshutt.staracademy.init.ModBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class SafariPortal {

    private static final AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK = (state, world, pos) -> state.isOf(ModBlocks.SAFARI_PORTAL_FRAME.get());
    private final WorldAccess world;
    private final Direction.Axis axis;
    private final Direction negativeDir;
    private int foundPortalBlocks;
    private BlockPos lowerCorner;
    private int height;
    private final int width;

    public static Optional<SafariPortal> getNewPortal(WorldAccess world, BlockPos pos, Direction.Axis axis) {
        return getOrEmpty(world, pos, areaHelper -> areaHelper.isValid() && areaHelper.foundPortalBlocks == 0, axis);
    }

    public static Optional<SafariPortal> getOrEmpty(WorldAccess world, BlockPos pos, Predicate<SafariPortal> validator, Direction.Axis axis) {
        Optional<SafariPortal> optional = Optional.of(new SafariPortal(world, pos, axis)).filter(validator);
        if (optional.isPresent()) {
            return optional;
        } else {
            Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(new SafariPortal(world, pos, axis2)).filter(validator);
        }
    }

    public SafariPortal(WorldAccess world, BlockPos pos, Direction.Axis axis) {
        this.world = world;
        this.axis = axis;
        this.negativeDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.lowerCorner = this.getLowerCorner(pos);
        if (this.lowerCorner == null) {
            this.lowerCorner = pos;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.getWidth();
            if (this.width > 0) {
                this.height = this.getHeight();
            }
        }
    }

    @Nullable
    private BlockPos getLowerCorner(BlockPos pos) {
        int i = Math.max(this.world.getBottomY(), pos.getY() - 21);

        while (pos.getY() > i && validStateInsidePortal(this.world.getBlockState(pos.down()))) {
            pos = pos.down();
        }

        Direction direction = this.negativeDir.getOpposite();
        int j = this.getWidth(pos, direction) - 1;
        return j < 0 ? null : pos.offset(direction, j);
    }

    private int getWidth() {
        int i = this.getWidth(this.lowerCorner, this.negativeDir);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private int getWidth(BlockPos pos, Direction direction) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; i <= 21; i++) {
            mutable.set(pos).move(direction, i);
            BlockState blockState = this.world.getBlockState(mutable);
            if (!validStateInsidePortal(blockState)) {
                if (IS_VALID_FRAME_BLOCK.test(blockState, this.world, mutable)) {
                    return i;
                }
                break;
            }

            BlockState blockState2 = this.world.getBlockState(mutable.move(Direction.DOWN));
            if (!IS_VALID_FRAME_BLOCK.test(blockState2, this.world, mutable)) {
                break;
            }
        }

        return 0;
    }

    private int getHeight() {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = this.getPotentialHeight(mutable);
        return i >= 3 && i <= 21 && this.isHorizontalFrameValid(mutable, i) ? i : 0;
    }

    private boolean isHorizontalFrameValid(BlockPos.Mutable pos, int height) {
        for (int i = 0; i < this.width; i++) {
            BlockPos.Mutable mutable = pos.set(this.lowerCorner).move(Direction.UP, height).move(this.negativeDir, i);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable), this.world, mutable)) {
                return false;
            }
        }

        return true;
    }

    private int getPotentialHeight(BlockPos.Mutable pos) {
        for (int i = 0; i < 21; i++) {
            pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, -1);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(pos), this.world, pos)) {
                return i;
            }

            pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, this.width);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(pos), this.world, pos)) {
                return i;
            }

            for (int j = 0; j < this.width; j++) {
                pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
                BlockState blockState = this.world.getBlockState(pos);
                if (!validStateInsidePortal(blockState)) {
                    return i;
                }

                if (blockState.isOf(ModBlocks.SAFARI_PORTAL.get())) {
                    this.foundPortalBlocks++;
                }
            }
        }

        return 21;
    }

    private static boolean validStateInsidePortal(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(ModBlocks.SAFARI_PORTAL.get());
    }

    public boolean isValid() {
        return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortal() {
        BlockState blockState = ModBlocks.SAFARI_PORTAL.get().getDefaultState().with(SafariPortalBlock.AXIS, this.axis);
        BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1))
                .forEach(blockPos -> this.world.setBlockState(blockPos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));
    }

    public boolean wasAlreadyValid() {
        return this.isValid() && this.foundPortalBlocks == this.width * this.height;
    }
    
}
