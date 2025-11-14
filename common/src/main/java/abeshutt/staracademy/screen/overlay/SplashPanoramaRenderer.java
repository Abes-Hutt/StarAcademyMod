package abeshutt.staracademy.screen.overlay;

import abeshutt.staracademy.util.ClientScheduler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class SplashPanoramaRenderer extends RotatingCubeMapRenderer {

    public SplashPanoramaRenderer() {
        super(null);
    }

    @Override
    public void render(DrawContext context, int width, int height, float alpha, float tickDelta) {
        RenderSystem.enableBlend();
        context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        double time = ClientScheduler.getTick(tickDelta);

        SplashLoader.getFrame(time).ifPresent(id -> {
            if (MinecraftClient.getInstance().getTextureManager()
                    .getOrDefault(id, null) instanceof NativeImageBackedTexture texture) {
                if (texture.getImage() != null) {
                    int w = texture.getImage().getWidth();
                    int h = texture.getImage().getHeight();

                    context.getMatrices().push();
                    context.getMatrices().scale((float)width / w, (float)height / h, 1.0f);
                    context.drawTexture(id, 0, 0, 0, 0, w, h, w, h);
                    context.getMatrices().pop();
                }
            }
        });

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

}
