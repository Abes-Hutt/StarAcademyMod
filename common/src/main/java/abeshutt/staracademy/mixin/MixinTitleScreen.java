package abeshutt.staracademy.mixin;

import abeshutt.staracademy.screen.overlay.SplashLoader;
import abeshutt.staracademy.util.ClientScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "renderPanoramaBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(DrawContext context, float delta, CallbackInfo ci) {
        double time = ClientScheduler.getTick(delta);

        SplashLoader.getFrame(time).ifPresent(id -> {
            if (MinecraftClient.getInstance().getTextureManager()
                    .getOrDefault(id, null) instanceof NativeImageBackedTexture texture) {
                if (texture.getImage() != null) {
                    int width = texture.getImage().getWidth();
                    int height = texture.getImage().getHeight();

                    context.getMatrices().push();
                    context.getMatrices().scale((float)this.width / width, (float)this.height / height, 1.0f);
                    context.drawTexture(id, 0, 0, 0, 0, width, height, width, height);
                    context.getMatrices().pop();
                }
            }
        });

        ci.cancel();
    }

}
