package abeshutt.staracademy.compat.enhancedcelestials.block.entity.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.client.RenderUtil;
import abeshutt.staracademy.compat.enhancedcelestials.block.LunarForecastHologramBlock;
import abeshutt.staracademy.compat.enhancedcelestials.block.entity.LunarForecastHologramBlockEntity;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials.api.lunarevent.client.LunarEventClientSettings;
import dev.corgitaco.enhancedcelestials.lunarevent.EnhancedCelestialsLunarForecastWorldData;
import dev.corgitaco.enhancedcelestials.lunarevent.LunarEventInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;

public class LunarForecastHologramBlockEntityRenderer implements BlockEntityRenderer<LunarForecastHologramBlockEntity> {

    public LunarForecastHologramBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(LunarForecastHologramBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.getCachedState().get(LunarForecastHologramBlock.LIT)) {
            return;
        }
        if (entity.getWorld() == null) {
            return;
        }
        EnhancedCelestialsLunarForecastWorldData data = EnhancedCelestials.lunarForecastWorldData(entity.getWorld()).orElse(null);
        if (data == null) {
            return;
        }

        LunarEventInstance lunarEventInstance = lunarEventInstance(entity);


        RegistryEntry<LunarEvent> lunarEventRegistryEntry = getEvent(entity, lunarEventInstance);

        float r = 0F;
        float g = 0F;
        float b = 0F;
        if (lunarEventRegistryEntry != null) {
            LunarEventClientSettings clientSettings = lunarEventRegistryEntry.value().getClientSettings();
            r = clientSettings.colorSettings().getGLMoonColor().x;
            g = clientSettings.colorSettings().getGLMoonColor().y;
            b = clientSettings.colorSettings().getGLMoonColor().z;
        }

        RenderUtil.renderHologram(matrices, vertexConsumers, overlay, r, g, b);
        renderMoon(entity, matrices, vertexConsumers, overlay);
        renderText(matrices, vertexConsumers, lunarEventRegistryEntry == null ? null : lunarEventRegistryEntry.value(), lunarEventInstance, data.getCurrentDay(), entity.getWorld());
    }



    private static void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable LunarEvent nextLunarEvent, @Nullable LunarEventInstance lunarEventInstance, long currentDay, World world) {
        matrices.push();
        matrices.translate(0.5, 2.6, 0.5);
        matrices.scale(0.025F, -0.025F, 0.025F);

        MinecraftClient client = MinecraftClient.getInstance();
        float cameraYaw = client.getEntityRenderDispatcher().camera.getYaw();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));


        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text moonName = nextLunarEvent == null ? Text.literal("???") : nextLunarEvent.getTextComponents().name().getComponent();
        int moonNameWidth = textRenderer.getWidth(moonName);
        float moonNameHalfWidth = (float) (-moonNameWidth / 2);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        long daysUntil = lunarEventInstance == null ? Long.MIN_VALUE : lunarEventInstance.getDaysUntil(currentDay);

        Text timeUntil;
        if (daysUntil == Long.MIN_VALUE) {
            timeUntil = Text.literal("No Upcoming Lunar Events");
        } else if (daysUntil == 0) {
            timeUntil = world.isDay() ? Text.literal("Tonight") : Text.literal("Now");
        } else if (daysUntil == 1) {
            timeUntil = Text.literal("Tomorrow");
        } else {
            timeUntil = Text.literal("In %d days".formatted(daysUntil));
        }

        float timeUntilWith = (float) (-textRenderer.getWidth(timeUntil) / 2);


        float recipricalScale = 1;
        float scale = 1.0F;
        if (moonNameWidth > 10) {
            recipricalScale = MathHelper.clampedLerp(1.0F, 0.1F, moonNameWidth / 250F);
            scale = 1 / recipricalScale;
        }

        matrices.scale(recipricalScale, recipricalScale, recipricalScale);
        textRenderer.draw(moonName, moonNameHalfWidth, 0, ColorHelper.Argb.getArgb(255, 255, 255, 255), false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, ColorHelper.Argb.getArgb(0, 0, 0, 0), LightmapTextureManager.MAX_LIGHT_COORDINATE);
        matrices.scale(scale, scale, scale);
        textRenderer.draw(timeUntil, timeUntilWith, MinecraftClient.getInstance().textRenderer.fontHeight + 1, ColorHelper.Argb.getArgb(255, 255, 255, 255), false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, ColorHelper.Argb.getArgb(0, 0, 0, 0), LightmapTextureManager.MAX_LIGHT_COORDINATE);
        matrices.pop();
    }

    @Nullable
    private static RegistryEntry<LunarEvent> getEvent(LunarForecastHologramBlockEntity entity, @Nullable LunarEventInstance lunarEventInstance) {
        if (lunarEventInstance == null) {
            return null;
        }
        return lunarEventInstance.getEvent(entity.getWorld().getRegistryManager().get(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY));
    }

    private static void renderMoon(LunarForecastHologramBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }
        LunarEventInstance lunarEventInstance = lunarEventInstance(entity);
        if (lunarEventInstance == null) {
            return;
        }
        RegistryEntry<LunarEvent> event = getEvent(entity, lunarEventInstance);


        LunarEventClientSettings clientSettings = event.value().getClientSettings();
        Identifier moonTexture = clientSettings.moonTextureLocation();

        float r = 255;
        float g = 255;
        float b = 255;
        if (moonTexture.equals(WorldRenderer.MOON_PHASES)) {
            moonTexture = StarAcademyMod.id("textures/lunar/display/moon.png");
            r = clientSettings.colorSettings().getGLMoonColor().x;
            g = clientSettings.colorSettings().getGLMoonColor().y;
            b = clientSettings.colorSettings().getGLMoonColor().z;
        }
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(moonTexture, true));
       RenderUtil. renderCameraOrientedQuad(matrices, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, r, g, b, 0.8F, consumer, stack -> {
            stack.translate(0.5, 3.15, 0.5);
            stack.scale(1, 1, 1);
        });
    }

    @Nullable
    public static LunarEventInstance lunarEventInstance(LunarForecastHologramBlockEntity entity) {
        return EnhancedCelestials.lunarForecastWorldData(entity.getWorld()).map(data -> data.getForecast().get(Math.clamp(0, data.getForecast().size() - 1, entity.getIdx()))).orElse(null);
    }
}