package abeshutt.staracademy.mixin.sophisticated;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = { "net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper.HopperUpgradeWrapper" })
public class MixinHopperUpgradeWrapper {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(Entity entity, World level, BlockPos pos, CallbackInfo ci) {
        ci.cancel();
    }

}
