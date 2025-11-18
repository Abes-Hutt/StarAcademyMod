package abeshutt.staracademy.mixin;

import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.screen.overlay.AcademySplashOverlay;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {

    @Shadow @Final @Mutable private static int MOJANG_RED;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void clinit(CallbackInfo ci) {
        MOJANG_RED = 0xFFA63D55;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ModConfigs.SCREEN.isEnabled()) {
            AcademySplashOverlay.render((SplashOverlay)(Object)this, context, mouseX, mouseY, delta);
        }
    }

}
