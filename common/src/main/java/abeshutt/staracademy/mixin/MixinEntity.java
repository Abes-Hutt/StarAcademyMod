package abeshutt.staracademy.mixin;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.block.SafariPortalBlock;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.init.ModWorldData;
import abeshutt.staracademy.util.ProxyEntity;
import abeshutt.staracademy.world.data.EntityState;
import abeshutt.staracademy.world.data.SafariData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity implements ProxyEntity {

    @Unique private boolean inSafariPortal;
    @Unique private boolean safariPortalCooldown;
    @Unique private List<Runnable> scheduledPortalTicks = new ArrayList<>();

    @Shadow public abstract World getWorld();
    @Shadow public abstract UUID getUuid();

    @Shadow protected abstract void checkBlockCollision();

    @Shadow public abstract boolean hasPortalCooldown();

    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract void readNbt(NbtCompound nbt);

    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Shadow public abstract void discard();

    @Override
    public boolean isInSafariPortal() {
        return this.inSafariPortal;
    }

    @Override
    public boolean hasSafariPortalCooldown() {
        return this.safariPortalCooldown;
    }

    @Override
    public void setInSafariPortal(boolean inSafariPortal) {
        this.inSafariPortal = inSafariPortal;
    }

    @Override
    public void setSafariPortalCooldown(boolean safariPortalCooldown) {
        this.safariPortalCooldown = safariPortalCooldown;
    }

    @Override
    public void schedulePortalTick(Runnable runnable) {
        this.scheduledPortalTicks.add(runnable);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tickHead(CallbackInfo ci) {
        if(ModConfigs.ENTITY_YEETER.contains((Entity)(Object)this) && !this.getWorld().isClient) {
            this.discard();
            ci.cancel();
        }

        this.setInSafariPortal(false);

        if(this.hasSafariPortalCooldown() && !this.hasPortalCooldown()) {
            if(!this.isCollidingWithPortal()) {
                this.setSafariPortalCooldown(false);
            }
        }
    }

    @Inject(method = "tickPortal", at = @At("HEAD"))
    public void tickPortal(CallbackInfo ci) {
        if(this.getWorld() instanceof ServerWorld) {
            this.scheduledPortalTicks.forEach(Runnable::run);
            this.scheduledPortalTicks.clear();
        }
    }

    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    protected void getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> ci) {
        if(destination.getRegistryKey() == StarAcademyMod.SAFARI) {
            BlockPos pos = ModConfigs.SAFARI.getPlacementOffset().add(ModConfigs.SAFARI.getRelativeSpawnPosition());

            ci.setReturnValue(new TeleportTarget(new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D),
                    Vec3d.ZERO, ModConfigs.SAFARI.getSpawnYaw(), ModConfigs.SAFARI.getSpawnPitch()));
        } else if(this.getWorld().getRegistryKey() == StarAcademyMod.SAFARI) {
            SafariData.Entry entry = ModWorldData.SAFARI.getGlobal(this.getWorld()).get(this.getUuid()).orElseThrow();
            EntityState state = entry.getLastState();
            ci.setReturnValue(new TeleportTarget(state.getPos(),Vec3d.ZERO, state.getYaw(), state.getPitch()));
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> ci) {
        Adapters.BOOLEAN.writeNbt(this.inSafariPortal).ifPresent(tag -> nbt.put("inSafariPortal", tag));
        Adapters.BOOLEAN.writeNbt(this.safariPortalCooldown).ifPresent(tag -> nbt.put("safariPortalCooldown", tag));
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.inSafariPortal = Adapters.BOOLEAN.readNbt(nbt.get("inSafariPortal")).orElse(false);
        this.safariPortalCooldown = Adapters.BOOLEAN.readNbt(nbt.get("safariPortalCooldown")).orElse(false);
    }

    private boolean isCollidingWithPortal() {
        Box box = this.getBoundingBox();
        BlockPos blockPos = BlockPos.ofFloored(box.minX + 1.0E-7, box.minY + 1.0E-7, box.minZ + 1.0E-7);
        BlockPos blockPos2 = BlockPos.ofFloored(box.maxX - 1.0E-7, box.maxY - 1.0E-7, box.maxZ - 1.0E-7);

        if(this.getWorld().isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for(int i = blockPos.getX(); i <= blockPos2.getX(); i++) {
                for(int j = blockPos.getY(); j <= blockPos2.getY(); j++) {
                    for(int k = blockPos.getZ(); k <= blockPos2.getZ(); k++) {
                        mutable.set(i, j, k);
                        BlockState blockState = this.getWorld().getBlockState(mutable);

                        if(blockState.getBlock() instanceof SafariPortalBlock) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

}
