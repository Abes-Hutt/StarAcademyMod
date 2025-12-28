package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModConfigs;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract World getWorld();
    @Shadow public abstract void discard();
    @Shadow public abstract MinecraftServer getServer();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tickHead(CallbackInfo ci) {
        if(!this.getWorld().isClient && this.getServer().getTicks() % 5 == 0 && ModConfigs.ENTITY_YEETER.contains((Entity)(Object)this)) {
            this.discard();
            ci.cancel();
        }
    }

    /* TODO: fix this
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
    }*/

}
