package abeshutt.staracademy.mixin.gliding;

import com.l33tfox.gliding.util.GliderClientUtil;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GliderClientUtil.class)
public class MixinGliderClientUtil {

    @Inject(method = "glidingTick", at = @At("HEAD"), cancellable = true)
    private static void glidingTick(MinecraftClient client, CallbackInfo ci) {
        if(client.isPaused()) {
            ci.cancel();
        }
    }

}
