package abeshutt.staracademy.mixin.mythsandlegends;

import com.github.d0ctorleon.mythsandlegends.utils.PlayerDataUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = { "com.github.d0ctorleon.mythsandlegends.utils.PlayerDataUtils" })
public class MixinPlayerDataUtils {

    @Inject(method = "checkPlayerInventory", at = @At("HEAD"), cancellable = true)
    private static void checkPlayerInventory(PlayerEntity player, CallbackInfo ci) {
        if(!(player instanceof ServerPlayerEntity)) {
            ci.cancel();
        }
    }

}
