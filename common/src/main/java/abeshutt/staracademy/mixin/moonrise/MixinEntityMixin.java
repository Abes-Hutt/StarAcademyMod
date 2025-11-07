package abeshutt.staracademy.mixin.moonrise;

import ca.spottedleaf.moonrise.patches.collisions.CollisionUtil;
import ca.spottedleaf.moonrise.patches.collisions.shape.CollisionVoxelShape;
import com.bawnorton.mixinsquared.TargetHandler;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Debug(export = true)
@Mixin(value = Entity.class, priority = 1500)
public abstract class MixinEntityMixin {

    @Shadow private World world;
    @Shadow private boolean onGround;

    @Shadow public abstract Box getBoundingBox();
    @Shadow public abstract float getStepHeight();

    @TargetHandler(mixin = "ca.spottedleaf.moonrise.mixin.collisions.EntityMixin", name = "adjustMovementForCollisions")
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "HEAD"), cancellable = true)
    private void processConditions(Vec3d movement, CallbackInfoReturnable<Vec3d> ci) {
        Entity entity = (Entity)(Object)this;
        boolean xZero = movement.x == 0.0;
        boolean yZero = movement.y == 0.0;
        boolean zZero = movement.z == 0.0;

        if (xZero & yZero & zZero) {
            ci.setReturnValue(movement);
        } else {
            Box currentBox = this.getBoundingBox();
            List<VoxelShape> potentialCollisionsVoxel = new ArrayList<>();
            List<Box> potentialCollisionsBB = new ArrayList<>();
            Box initialCollisionBox;
            if (xZero & zZero) {
                initialCollisionBox = movement.y < 0.0 ? CollisionUtil.cutDownwards(currentBox, movement.y) : CollisionUtil.cutUpwards(currentBox, movement.y);
            } else {
                initialCollisionBox = currentBox.stretch(movement);
            }

            List<Box> entityAABBs = new ArrayList<>();
            CollisionUtil.getEntityHardCollisions(this.world, entity, initialCollisionBox, entityAABBs, 0, (Predicate)null);
            CollisionUtil.getCollisionsForBlocksOrWorldBorder(this.world, entity, initialCollisionBox, potentialCollisionsVoxel, potentialCollisionsBB, 4, (BiPredicate)null);
            potentialCollisionsBB.addAll(entityAABBs);
            Vec3d collided = CollisionUtil.performCollisions(movement, currentBox, potentialCollisionsVoxel, potentialCollisionsBB);
            boolean collidedX = collided.x != movement.x;
            boolean collidedY = collided.y != movement.y;
            boolean collidedZ = collided.z != movement.z;
            boolean collidedDownwards = collidedY && movement.y < 0.0;
            double stepHeight;

            if (entity instanceof PokemonEntity && entity.hasControllingPassenger()) {
                BlockPos below = entity.getBlockPos().down();
                World level = entity.getWorld();
                var blockStateBelow = level.getBlockState(below);
                boolean isAirOrLiquid = blockStateBelow.isAir() || !blockStateBelow.getFluidState().isEmpty();
                boolean canSupportEntity = blockStateBelow.isSideSolidFullSquare(level, below, Direction.UP);
                boolean standingOnSolid = canSupportEntity && !isAirOrLiquid;
                if (standingOnSolid) {
                    ci.setReturnValue(collided);
                }
            }

            if ((collidedDownwards || this.onGround) && (collidedX || collidedZ) && !((stepHeight = this.getStepHeight()) <= 0.0)) {
                Box collidedYBox = collidedDownwards ? currentBox.offset(0.0, collided.y, 0.0) : currentBox;
                Box stepRetrievalBox = collidedYBox.stretch(movement.x, stepHeight, movement.z);
                if (!collidedDownwards) {
                    stepRetrievalBox = stepRetrievalBox.stretch(0.0, -9.999999747378752E-6, 0.0);
                }

                List<VoxelShape> stepVoxels = new ArrayList<>();
                CollisionUtil.getCollisionsForBlocksOrWorldBorder(this.world, entity, stepRetrievalBox, stepVoxels, entityAABBs, 4, null);
                float[] var21 = academy$calculateStepHeights(collidedYBox, stepVoxels, entityAABBs, (float)stepHeight, (float)collided.y);
                int var22 = var21.length;

                for(int var23 = 0; var23 < var22; ++var23) {
                    float step = var21[var23];
                    Vec3d stepResult = CollisionUtil.performCollisions(new Vec3d(movement.x, (double)step, movement.z), collidedYBox, stepVoxels, entityAABBs);
                    if (stepResult.horizontalLengthSquared() > collided.horizontalLengthSquared()) {
                        ci.setReturnValue(stepResult.add(0.0, collidedYBox.minY - currentBox.minY, 0.0));
                    }
                }

                ci.setReturnValue(collided);
            } else {
                ci.setReturnValue(collided);
            }
        }
    }

    @Unique
    private static float[] academy$calculateStepHeights(Box box, List<VoxelShape> voxels, List<Box> aabbs, float stepHeight, float collidedY) {
        FloatArraySet ret = new FloatArraySet();
        int i = 0;

        int len;
        for(len = voxels.size(); i < len; ++i) {
            VoxelShape shape = voxels.get(i);
            double[] yCoords = ((CollisionVoxelShape)shape).moonrise$rootCoordinatesY();
            double yOffset = ((CollisionVoxelShape)shape).moonrise$offsetY();

            for (double yUnoffset : yCoords) {
                double y = yUnoffset + yOffset;
                float step = (float) (y - box.minY);
                if (step > stepHeight) {
                    break;
                }

                if (!(step < 0.0F) && step != collidedY) {
                    ret.add(step);
                }
            }
        }

        i = 0;

        for(len = aabbs.size(); i < len; ++i) {
            Box shape = aabbs.get(i);
            float step1 = (float)(shape.minY - box.minY);
            float step2 = (float)(shape.maxY - box.minY);
            if (!(step1 < 0.0F) && step1 != collidedY && !(step1 > stepHeight)) {
                ret.add(step1);
            }

            if (!(step2 < 0.0F) && step2 != collidedY && !(step2 > stepHeight)) {
                ret.add(step2);
            }
        }

        float[] steps = ret.toFloatArray();
        FloatArrays.unstableSort(steps);
        return steps;
    }

}
