package abeshutt.staracademy.mixin;

import abeshutt.staracademy.util.AttributeHolder;
import abeshutt.staracademy.util.ProxyGymData;
import dev.architectury.platform.Platform;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if(Platform.isModLoaded("rad-gyms")) {
            AttributeHolder.setRoot(this, AttributeHolder.getRoot(oldPlayer));
            ProxyGymData.setGymData(this, ProxyGymData.getGymData(oldPlayer));
        }
    }

}
