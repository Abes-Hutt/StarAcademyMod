package abeshutt.staracademy.screen.overlay;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.util.ClientScheduler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class AcademySplashOverlay {

    public static void render(SplashOverlay splash, DrawContext context, int mouseX, int mouseY, float delta) {
        int i = context.getScaledWindowWidth();
        int j = context.getScaledWindowHeight();
        long l = Util.getMeasuringTimeMs();

        if (splash.reloading && splash.reloadStartTime == -1L) {
            splash.reloadStartTime = l;
        }

        int color = SplashOverlay.BRAND_ARGB.getAsInt();
        color = 0xFFFFFF00;

        float f = splash.reloadCompleteTime > -1L ? (float)(l - splash.reloadCompleteTime) / 1000.0F : -1.0F;
        float g = splash.reloadStartTime > -1L ? (float)(l - splash.reloadStartTime) / 500.0F : -1.0F;
        double time = ClientScheduler.getTick(delta);

        float h;
        if (f >= 1.0F) {
            if (splash.client.currentScreen != null) {
                //splash.client.currentScreen.render(context, 0, 0, delta);
            }

            int k = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);

            SplashLoader.getFrame(time).ifPresent(id -> {
                if (MinecraftClient.getInstance().getTextureManager()
                        .getOrDefault(id, null) instanceof NativeImageBackedTexture texture) {
                    if (texture.getImage() != null) {
                        int width = texture.getImage().getWidth();
                        int height = texture.getImage().getHeight();

                        context.getMatrices().push();
                        context.getMatrices().scale((float)i / width, (float)j / height, 1.0f);
                        context.drawTexture(id, 0, 0, 0, 0, width, height, width, height);
                        context.getMatrices().pop();
                    }
                }
            });

            h = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (splash.reloading) {
            if (splash.client.currentScreen != null && g < 1.0F) {
                //splash.client.currentScreen.render(context, mouseX, mouseY, delta);
            }

            h = MathHelper.clamp(g, 0.0F, 1.0F);

            int k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);

            SplashLoader.getFrame(time).ifPresent(id -> {
                if (MinecraftClient.getInstance().getTextureManager()
                        .getOrDefault(id, null) instanceof NativeImageBackedTexture texture) {
                    if (texture.getImage() != null) {
                        int width = texture.getImage().getWidth();
                        int height = texture.getImage().getHeight();

                        context.getMatrices().push();
                        context.getMatrices().scale((float)i / width, (float)j / height, 1.0f);
                        context.drawTexture(id, 0, 0, 0, 0, width, height, width, height);
                        context.getMatrices().pop();
                    }
                }
            });

            h = MathHelper.clamp(g, 0.0F, 1.0F);
        } else {
            int k = SplashOverlay.BRAND_ARGB.getAsInt();
            float m = (k >> 16 & 0xFF) / 255.0F;
            float n = (k >> 8 & 0xFF) / 255.0F;
            float o = (k & 0xFF) / 255.0F;
            GlStateManager._clearColor(m, n, o, 1.0F);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            h = 1.0F;

            SplashLoader.getFrame(time).ifPresent(id -> {
                if (MinecraftClient.getInstance().getTextureManager()
                        .getOrDefault(id, null) instanceof NativeImageBackedTexture texture) {
                    if (texture.getImage() != null) {
                        int width = texture.getImage().getWidth();
                        int height = texture.getImage().getHeight();

                        context.getMatrices().push();
                        context.getMatrices().scale((float)i / width, (float)j / height, 1.0f);
                        context.drawTexture(id, 0, 0, 0, 0, width, height, width, height);
                        context.getMatrices().pop();
                    }
                }
            });
        }

        int k = (int)(context.getScaledWindowWidth() * 0.5);
        int p = (int)(context.getScaledWindowHeight() * 0.5);
        double d = Math.min(context.getScaledWindowWidth() * 0.75, context.getScaledWindowHeight()) * 0.25;
        int q = (int)(d * 0.5);
        double e = d * 4.0;
        int r = (int)(e * 0.5);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        //RenderSystem.blendFunc(770, 1);
        context.setShaderColor(1.0F, 1.0F, 1.0F, h);
        //context.drawTexture(SplashOverlay.LOGO, k - r, p - q, r, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
        //context.drawTexture(SplashOverlay.LOGO, k, p - q, r, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);

        Identifier logoId = StarAcademyMod.id("splash/logo");

        if (MinecraftClient.getInstance().getTextureManager()
                .getOrDefault(logoId, null) instanceof NativeImageBackedTexture texture) {
            if (texture.getImage() != null) {
                int width = texture.getImage().getWidth();
                int height = texture.getImage().getHeight();

                context.getMatrices().push();
                float scale = (i / 2.0f) / width;
                context.getMatrices().scale(scale, scale, 1.0f);
                context.getMatrices().translate((i - width * scale) / 2.0f / scale, (j - height * scale) / 2.0f / scale, 1.0f);

                context.drawTexture(logoId, 0, 0, 0, 0, width, height, width, height);
                context.getMatrices().pop();
            }
        }

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        int s = (int)(context.getScaledWindowHeight() * 0.8325);
        float t = splash.reload.getProgress();
        splash.progress = MathHelper.clamp(splash.progress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);
        if (f < 1.0F) {
            splash.renderProgressBar(context, i / 2 - r, s - 5, i / 2 + r, s + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
        }

        if (f >= 2.0F) {
            splash.client.setOverlay(null);
        }

        if (splash.reloadCompleteTime == -1L && splash.reload.isComplete() && (!splash.reloading || g >= 2.0F)) {
            try {
                splash.reload.throwException();
                splash.exceptionHandler.accept(Optional.empty());
            } catch (Throwable var23) {
                splash.exceptionHandler.accept(Optional.of(var23));
            }

            splash.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (splash.client.currentScreen != null) {
                splash.client.currentScreen.init(splash.client, context.getScaledWindowWidth(), context.getScaledWindowHeight());
            }
        }
    }

}
