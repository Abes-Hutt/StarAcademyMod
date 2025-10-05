package abeshutt.staracademy.mixin.outbreaks;

import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = { "com.scouter.cobblemonoutbreaks.util.PortalUtils" })
public class MixinPortalUtils {

    @Inject(method = "processOutbreaks", at = @At("HEAD"), cancellable = true)
    private static void processOutbreaks(ServerPlayerEntity serverPlayer, int outbreakCount, CallbackInfo ci) {
        if (serverPlayer.getWorld().getRegistryKey() != StarAcademyMod.SAFARI) {
            ci.cancel();
        }
    }

}
