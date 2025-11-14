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
    public void render(DrawContext context, int screenWidth, int screenHeight, float alpha, float tickDelta) {
        RenderSystem.enableBlend();
        context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        double time = ClientScheduler.getTick(tickDelta);

        SplashLoader.getFrame(time).ifPresent(id -> {
            if (MinecraftClient.getInstance().getTextureManager()
                    .getOrDefault(id, null) instanceof NativeImageBackedTexture texture) {
                if (texture.getImage() != null) {
                    int frameWidth = texture.getImage().getWidth();
                    int frameHeight = texture.getImage().getHeight();
                    float scale, offsetX, offsetY;

                    if (frameWidth * screenHeight > frameHeight * screenWidth) {
                        // Frame is wider
                        scale = (float)screenHeight / frameHeight;
                        offsetX = (screenWidth - frameWidth * scale) / 2.0f;
                        offsetY = 0.0f;
                    } else {
                        // Frame is taller
                        scale = (float)screenWidth / frameWidth;
                        offsetX = 0.0f;
                        offsetY = (screenHeight - frameHeight * scale) / 2.0f;
                    }

                    context.getMatrices().push();
                    context.getMatrices().scale(scale, scale, 1.0f);
                    context.getMatrices().translate(offsetX / scale, offsetY / scale, 0.0f);
                    context.drawTexture(id, 0, 0, 0, 0, frameWidth, frameHeight,
                            frameWidth, frameHeight);
                    context.getMatrices().pop();
                }
            }
        });

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

}
