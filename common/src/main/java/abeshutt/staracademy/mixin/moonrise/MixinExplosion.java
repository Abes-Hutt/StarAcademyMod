package abeshutt.staracademy.mixin.moonrise;

import ca.spottedleaf.moonrise.common.PlatformHooks;
import ca.spottedleaf.moonrise.common.util.CoordinateUtils;
import ca.spottedleaf.moonrise.patches.collisions.CollisionUtil;
import ca.spottedleaf.moonrise.patches.collisions.ExplosionBlockCache;
import ca.spottedleaf.moonrise.patches.collisions.block.CollisionBlockState;
import ca.spottedleaf.moonrise.patches.getblock.GetBlockChunk;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin({Explosion.class})
abstract class MixinExplosion {

    @Shadow @Final private World world;
    @Shadow @Final private Entity entity;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final private ExplosionBehavior behavior;
    @Shadow @Final private float power;
    @Shadow @Final private ObjectArrayList<BlockPos> affectedBlocks;
    @Shadow @Final private Map<PlayerEntity, Vec3d> affectedPlayers;
    @Shadow @Final private boolean createFire;
    @Shadow @Final private DamageSource damageSource;
    @Unique private static final double[] CACHED_RAYS;
    @Unique private static final int CHUNK_CACHE_SHIFT = 2;
    @Unique private static final int CHUNK_CACHE_MASK = 3;
    @Unique private static final int CHUNK_CACHE_WIDTH = 4;
    @Unique private static final int BLOCK_EXPLOSION_CACHE_SHIFT = 3;
    @Unique private static final int BLOCK_EXPLOSION_CACHE_MASK = 7;
    @Unique private static final int BLOCK_EXPLOSION_CACHE_WIDTH = 8;
    @Unique private static final Float ZERO_RESISTANCE;
    @Unique private Long2ObjectOpenHashMap<ExplosionBlockCache> blockCache = null;
    @Unique private long[] chunkPosCache = null;
    @Unique private WorldChunk[] chunkCache = null;

    @Unique
    private ExplosionBlockCache getOrCacheExplosionBlock(int x, int y, int z, long key, boolean calculateResistance) {
        ExplosionBlockCache ret = this.blockCache.get(key);

        if (ret != null) {
            return ret;
        } else {
            BlockPos pos = new BlockPos(x, y, z);
            if (!this.world.isInBuildLimit(pos)) {
                ret = new ExplosionBlockCache(key, pos, (BlockState)null, (FluidState)null, 0.0F, true);
            } else {
                long chunkKey = CoordinateUtils.getChunkKey(x >> 4, z >> 4);
                int chunkCacheKey = x >> 4 & 3 | z >> 4 << 2 & 12;
                WorldChunk chunk;
                if (this.chunkPosCache[chunkCacheKey] == chunkKey) {
                    chunk = this.chunkCache[chunkCacheKey];
                } else {
                    this.chunkPosCache[chunkCacheKey] = chunkKey;
                    this.chunkCache[chunkCacheKey] = chunk = this.world.getChunk(x >> 4, z >> 4);
                }

                BlockState blockState = ((GetBlockChunk)chunk).moonrise$getBlock(x, y, z);
                FluidState fluidState = blockState.getFluidState();
                Optional<Float> resistance = !calculateResistance ? Optional.empty() : this.behavior.getBlastResistance((Explosion)(Object)this, this.world, pos, blockState, fluidState);
                ret = new ExplosionBlockCache(key, pos, blockState, fluidState, ((Float)resistance.orElse(ZERO_RESISTANCE) + 0.3F) * 0.3F, false);
            }

            this.blockCache.put(key, ret);
            return ret;
        }
    }

    @Unique
    private boolean clipsAnything(Vec3d from, Vec3d to, CollisionUtil.LazyEntityCollisionContext context, ExplosionBlockCache[] blockCache, BlockPos.Mutable currPos) {
        double adjX = 1.0E-7 * (from.x - to.x);
        double adjY = 1.0E-7 * (from.y - to.y);
        double adjZ = 1.0E-7 * (from.z - to.z);
        if (adjX == 0.0 && adjY == 0.0 && adjZ == 0.0) {
            return false;
        } else {
            double toXAdj = to.x - adjX;
            double toYAdj = to.y - adjY;
            double toZAdj = to.z - adjZ;
            double fromXAdj = from.x + adjX;
            double fromYAdj = from.y + adjY;
            double fromZAdj = from.z + adjZ;
            int currX = MathHelper.floor(fromXAdj);
            int currY = MathHelper.floor(fromYAdj);
            int currZ = MathHelper.floor(fromZAdj);
            double diffX = toXAdj - fromXAdj;
            double diffY = toYAdj - fromYAdj;
            double diffZ = toZAdj - fromZAdj;
            double dxDouble = Math.signum(diffX);
            double dyDouble = Math.signum(diffY);
            double dzDouble = Math.signum(diffZ);
            int dx = (int)dxDouble;
            int dy = (int)dyDouble;
            int dz = (int)dzDouble;
            double normalizedDiffX = diffX == 0.0 ? Double.MAX_VALUE : dxDouble / diffX;
            double normalizedDiffY = diffY == 0.0 ? Double.MAX_VALUE : dyDouble / diffY;
            double normalizedDiffZ = diffZ == 0.0 ? Double.MAX_VALUE : dzDouble / diffZ;
            double normalizedCurrX = normalizedDiffX * (diffX > 0.0 ? 1.0 - MathHelper.fractionalPart(fromXAdj) : MathHelper.fractionalPart(fromXAdj));
            double normalizedCurrY = normalizedDiffY * (diffY > 0.0 ? 1.0 - MathHelper.fractionalPart(fromYAdj) : MathHelper.fractionalPart(fromYAdj));
            double normalizedCurrZ = normalizedDiffZ * (diffZ > 0.0 ? 1.0 - MathHelper.fractionalPart(fromZAdj) : MathHelper.fractionalPart(fromZAdj));

            while(true) {
                currPos.set(currX, currY, currZ);
                long key = BlockPos.asLong(currX, currY, currZ);
                int cacheKey = currX & 7 | (currY & 7) << 3 | (currZ & 7) << 6;
                ExplosionBlockCache cachedBlock = blockCache[cacheKey];
                if (cachedBlock == null || cachedBlock.key != key) {
                    blockCache[cacheKey] = cachedBlock = this.getOrCacheExplosionBlock(currX, currY, currZ, key, false);
                }

                BlockState blockState = cachedBlock.blockState;
                if (blockState != null && !((CollisionBlockState)blockState).moonrise$emptyContextCollisionShape()) {
                    VoxelShape collision = cachedBlock.cachedCollisionShape;
                    if (collision == null) {
                        collision = ((CollisionBlockState)blockState).moonrise$getConstantContextCollisionShape();
                        if (collision == null) {
                            collision = blockState.getCollisionShape(this.world, currPos, context);
                            if (!context.isDelegated()) {
                                cachedBlock.cachedCollisionShape = collision;
                            }
                        } else {
                            cachedBlock.cachedCollisionShape = collision;
                        }
                    }

                    if (!collision.isEmpty() && collision.raycast(from, to, currPos) != null) {
                        return true;
                    }
                }

                if (normalizedCurrX > 1.0 && normalizedCurrY > 1.0 && normalizedCurrZ > 1.0) {
                    return false;
                }

                if (normalizedCurrX < normalizedCurrY) {
                    if (normalizedCurrX < normalizedCurrZ) {
                        currX += dx;
                        normalizedCurrX += normalizedDiffX;
                    } else {
                        currZ += dz;
                        normalizedCurrZ += normalizedDiffZ;
                    }
                } else if (normalizedCurrY < normalizedCurrZ) {
                    currY += dy;
                    normalizedCurrY += normalizedDiffY;
                } else {
                    currZ += dz;
                    normalizedCurrZ += normalizedDiffZ;
                }
            }
        }
    }

    @Unique
    private float getSeenFraction(Vec3d source, Entity target, ExplosionBlockCache[] blockCache, BlockPos.Mutable blockPos) {
        Box boundingBox = target.getBoundingBox();
        double diffX = boundingBox.maxX - boundingBox.minX;
        double diffY = boundingBox.maxY - boundingBox.minY;
        double diffZ = boundingBox.maxZ - boundingBox.minZ;
        double incX = 1.0 / (diffX * 2.0 + 1.0);
        double incY = 1.0 / (diffY * 2.0 + 1.0);
        double incZ = 1.0 / (diffZ * 2.0 + 1.0);
        if (!(incX < 0.0) && !(incY < 0.0) && !(incZ < 0.0)) {
            double offX = (1.0 - Math.floor(1.0 / incX) * incX) * 0.5 + boundingBox.minX;
            double offY = boundingBox.minY;
            double offZ = (1.0 - Math.floor(1.0 / incZ) * incZ) * 0.5 + boundingBox.minZ;
            CollisionUtil.LazyEntityCollisionContext context = new CollisionUtil.LazyEntityCollisionContext(target);
            int totalRays = 0;
            int missedRays = 0;

            for(double dx = 0.0; dx <= 1.0; dx += incX) {
                double fromX = Math.fma(dx, diffX, offX);

                for(double dy = 0.0; dy <= 1.0; dy += incY) {
                    double fromY = Math.fma(dy, diffY, offY);

                    for(double dz = 0.0; dz <= 1.0; dz += incZ) {
                        ++totalRays;
                        Vec3d from = new Vec3d(fromX, fromY, Math.fma(dz, diffZ, offZ));
                        if (!this.clipsAnything(from, source, context, blockCache, blockPos)) {
                            ++missedRays;
                        }
                    }
                }
            }

            return (float)missedRays / (float)totalRays;
        } else {
            return 0.0F;
        }
    }


    /**
     * @reason Rewrite ray casting and seen fraction calculation for performance
     * @author Spottedleaf
     */
    @Inject(method = "collectBlocksAndDamageEntities", at = @At("HEAD"), cancellable = true)
    public void collectBlocksAndDamageEntities(CallbackInfo ci) {
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));

        this.blockCache = new Long2ObjectOpenHashMap<>();

        this.chunkPosCache = new long[CHUNK_CACHE_WIDTH * CHUNK_CACHE_WIDTH];
        Arrays.fill(this.chunkPosCache, ChunkPos.MARKER);

        this.chunkCache = new WorldChunk[CHUNK_CACHE_WIDTH * CHUNK_CACHE_WIDTH];

        final ExplosionBlockCache[] blockCache = new ExplosionBlockCache[BLOCK_EXPLOSION_CACHE_WIDTH * BLOCK_EXPLOSION_CACHE_WIDTH * BLOCK_EXPLOSION_CACHE_WIDTH];

        // use initial cache value that is most likely to be used: the source position
        final ExplosionBlockCache initialCache;
        {
            final int blockX = MathHelper.floor(this.x);
            final int blockY = MathHelper.floor(this.y);
            final int blockZ = MathHelper.floor(this.z);

            final long key = BlockPos.asLong(blockX, blockY, blockZ);

            initialCache = this.getOrCacheExplosionBlock(blockX, blockY, blockZ, key, true);
        }

        // only ~1/3rd of the loop iterations in vanilla will result in a ray, as it is iterating the perimeter of
        // a 16x16x16 cube
        // we can cache the rays and their normals as well, so that we eliminate the excess iterations / checks and
        // calculations in one go
        // additional aggressive caching of block retrieval is very significant, as at low power (i.e tnt) most
        // block retrievals are not unique
        for (int ray = 0, len = CACHED_RAYS.length; ray < len;) {
            ExplosionBlockCache cachedBlock = initialCache;

            double currX = this.x;
            double currY = this.y;
            double currZ = this.z;

            final double incX = CACHED_RAYS[ray];
            final double incY = CACHED_RAYS[ray + 1];
            final double incZ = CACHED_RAYS[ray + 2];

            ray += 3;

            float power = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);

            do {
                final int blockX = MathHelper.floor(currX);
                final int blockY = MathHelper.floor(currY);
                final int blockZ = MathHelper.floor(currZ);

                final long key = BlockPos.asLong(blockX, blockY, blockZ);

                if (cachedBlock.key != key) {
                    final int cacheKey =
                            (blockX & BLOCK_EXPLOSION_CACHE_MASK) |
                                    (blockY & BLOCK_EXPLOSION_CACHE_MASK) << (BLOCK_EXPLOSION_CACHE_SHIFT) |
                                    (blockZ & BLOCK_EXPLOSION_CACHE_MASK) << (BLOCK_EXPLOSION_CACHE_SHIFT + BLOCK_EXPLOSION_CACHE_SHIFT);
                    cachedBlock = blockCache[cacheKey];
                    if (cachedBlock == null || cachedBlock.key != key) {
                        blockCache[cacheKey] = cachedBlock = this.getOrCacheExplosionBlock(blockX, blockY, blockZ, key, true);
                    }
                }

                if (cachedBlock.outOfWorld) {
                    break;
                }

                power -= cachedBlock.resistance;

                if (power > 0.0f && cachedBlock.shouldExplode == null) {
                    // note: we expect shouldBlockExplode to be pure with respect to power, as Vanilla currently is.
                    // basically, it is unused, which allows us to cache the result
                    final boolean shouldExplode = this.behavior.canDestroyBlock((Explosion)(Object)this, this.world, cachedBlock.immutablePos, cachedBlock.blockState, power);
                    cachedBlock.shouldExplode = shouldExplode ? Boolean.TRUE : Boolean.FALSE;
                    if (shouldExplode) {
                        if (this.createFire || !cachedBlock.blockState.isAir()) {
                            this.affectedBlocks.add(cachedBlock.immutablePos);
                        }
                    }
                }

                power -= 0.22500001F;
                currX += incX;
                currY += incY;
                currZ += incZ;
            } while (power > 0.0f);
        }

        final double diameter = (double)this.power * 2.0;
        final List<Entity> entities = this.world.getOtherEntities(this.entity,
                new Box(
                        (double)MathHelper.floor(this.x - (diameter + 1.0)),
                        (double)MathHelper.floor(this.y - (diameter + 1.0)),
                        (double)MathHelper.floor(this.z - (diameter + 1.0)),

                        (double)MathHelper.floor(this.x + (diameter + 1.0)),
                        (double)MathHelper.floor(this.y + (diameter + 1.0)),
                        (double)MathHelper.floor(this.z + (diameter + 1.0))
                )
        );
        final Vec3d center = new Vec3d(this.x, this.y, this.z);

        final BlockPos.Mutable blockPos = new BlockPos.Mutable();

        final PlatformHooks platformHooks = PlatformHooks.get();

        platformHooks.onExplosion(this.world, (Explosion)(Object)this, entities, diameter);
        for (int i = 0, len = entities.size(); i < len; ++i) {
            final Entity entity = entities.get(i);
            if (entity.isImmuneToExplosion((Explosion)(Object)this)) {
                continue;
            }

            final double normalizedDistanceToCenter = Math.sqrt(entity.squaredDistanceTo(center)) / diameter;
            if (normalizedDistanceToCenter > 1.0) {
                continue;
            }

            double distX = entity.getX() - this.x;
            double distY = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
            double distZ = entity.getZ() - this.z;
            final double distMag = Math.sqrt(distX * distX + distY * distY + distZ * distZ);

            if (distMag == 0.0) {
                continue;
            }

            distX /= distMag;
            distY /= distMag;
            distZ /= distMag;

            // route to new visible fraction calculation, using the existing block cache
            final double seenFraction = (double)this.getSeenFraction(center, entity, blockCache, blockPos);
            if (this.behavior.shouldDamage((Explosion)(Object)this, entity)) {
                // inline getEntityDamageAmount so that we can avoid double calling getSeenPercent, which is the MOST
                // expensive part of this loop!!!!
                final double factor = (1.0 - normalizedDistanceToCenter) * seenFraction;
                entity.damage(this.damageSource, (float)((factor * factor + factor) / 2.0 * 7.0 * diameter + 1.0));
            }

            final double intensityFraction = (1.0 - normalizedDistanceToCenter) * seenFraction * (double)this.behavior.getKnockbackModifier(entity);


            final double knockbackFraction;
            if (entity instanceof LivingEntity livingEntity) {
                knockbackFraction = intensityFraction * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE));
            } else {
                knockbackFraction = intensityFraction;
            }

            Vec3d knockback = new Vec3d(distX * knockbackFraction, distY * knockbackFraction, distZ * knockbackFraction);
            knockback = platformHooks.modifyExplosionKnockback(this.world, (Explosion)(Object)this, entity, knockback);
            entity.setVelocity(entity.getVelocity().add(knockback));

            if (entity instanceof PlayerEntity player) {
                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                    this.affectedPlayers.put(player, knockback);
                }
            }

            entity.onExplodedBy(this.entity);
        }

        this.blockCache = null;
        this.chunkPosCache = null;
        this.chunkCache = null;
        ci.cancel();
    }

    static {
        DoubleArrayList rayCoords = new DoubleArrayList();

        for(int x = 0; x <= 15; ++x) {
            for(int y = 0; y <= 15; ++y) {
                for(int z = 0; z <= 15; ++z) {
                    if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
                        double xDir = (double)((float)x / 15.0F * 2.0F - 1.0F);
                        double yDir = (double)((float)y / 15.0F * 2.0F - 1.0F);
                        double zDir = (double)((float)z / 15.0F * 2.0F - 1.0F);
                        double mag = Math.sqrt(xDir * xDir + yDir * yDir + zDir * zDir);
                        rayCoords.add(xDir / mag * 0.30000001192092896);
                        rayCoords.add(yDir / mag * 0.30000001192092896);
                        rayCoords.add(zDir / mag * 0.30000001192092896);
                    }
                }
            }
        }

        CACHED_RAYS = rayCoords.toDoubleArray();
        ZERO_RESISTANCE = -0.3F;
    }

}

