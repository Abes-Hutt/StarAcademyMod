package abeshutt.staracademy.mixin;

import abeshutt.staracademy.config.sound.DimensionEnterPlaySoundEvent;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.net.PlaySoundS2CPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TeleportTarget.class)
public class MixinTeleportTarget {

    @Inject(method = "postDimensionTransition", at = @At("RETURN"), cancellable = true)
    public void postDimensionTransition(CallbackInfoReturnable<TeleportTarget.PostDimensionTransition> ci) {
        ci.setReturnValue(ci.getReturnValue().then(entity -> {
            if(entity instanceof ServerPlayerEntity player) {
                for(DimensionEnterPlaySoundEvent event : ModConfigs.SOUND_EVENTS.get(DimensionEnterPlaySoundEvent.class)) {
                    if(!player.getWorld().getRegistryKey().getValue().equals(event.getDimension())) {
                        continue;
                    }

                    NetworkManager.sendToPlayer(player, new PlaySoundS2CPacket(event.getSound()));
                }
            }
        }));
    }

}
