package abeshutt.staracademy.mixin.outbreaks;

import com.bawnorton.mixinsquared.TargetHandler;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import com.scouter.cobblemonoutbreaks.util.PortalUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = PlayerEntity.class, priority = 1500)
public class MixinPlayerEntity {

    @TargetHandler(mixin = "com.scouter.cobblemonoutbreaks.mixin.PlayerTickMixin", name = "outbreakPortal$playerTick")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    public void collectCloudsOnUse(CallbackInfo ci, CallbackInfo ci2) {
        if (OutbreakConfigManager.getConfig().getSpawningConfig().isPerPlayer()
                && (Object)this instanceof ServerPlayerEntity player) {
            int timer = OutbreakConfigManager.getConfig().getSpawningConfig().getPerPlayerOutbreakTimer();

            if (new Random().nextInt(timer) == 0) {
                PortalUtils.processOutbreaks(player, OutbreakConfigManager.getConfig().getSpawningConfig().getOutbreakSpawnCount());
            }
        }

        ci2.cancel();
    }

}
