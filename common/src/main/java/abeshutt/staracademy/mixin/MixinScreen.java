package abeshutt.staracademy.mixin;

import abeshutt.staracademy.config.ScreenConfig;
import abeshutt.staracademy.init.ModConfigs;
import abeshutt.staracademy.screen.overlay.SplashPanoramaRenderer;
import abeshutt.staracademy.screen.widget.StreamListWidget;
import abeshutt.staracademy.screen.widget.StreamWidget;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow public int height;

    @Shadow public abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Shadow @Mutable @Final protected static RotatingCubeMapRenderer ROTATING_PANORAMA_RENDERER;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void clinit(CallbackInfo ci) {
        ModConfigs.SCREEN = new ScreenConfig().read();

        if (ModConfigs.SCREEN.isEnabled()) {
            ROTATING_PANORAMA_RENDERER = new SplashPanoramaRenderer();
        }
    }

    @Inject(method = { "init(Lnet/minecraft/client/MinecraftClient;II)V", "clearAndInit" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V", shift = At.Shift.AFTER))
    private void init(CallbackInfo ci) {
        if (!ModConfigs.SCREEN.hasStreams()) {
            return;
        }

        if (!((Object)this instanceof TitleScreen) && !((Object)this instanceof GameMenuScreen)) {
            return;
        }

        int fit = Integer.MAX_VALUE;

        for (int i = 0; i < 50; i++) {
            int height = 2 + 2 + i * StreamWidget.HEIGHT + StreamListWidget.GAP * (i - 1);

            if (Math.abs(this.height / 2 - height) < Math.abs(this.height / 2 - fit)) {
                fit = height;
            }
        }

        StreamListWidget streamList = new StreamListWidget(0, (this.height - fit) / 2, 120, fit);
        this.addDrawableChild(streamList);
    }

}
