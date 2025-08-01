package abeshutt.staracademy.compat.enhancedcelestials.block.entity.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.compat.enhancedcelestials.block.entity.LunarForecastHologramBlockEntity;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials.api.lunarevent.client.LunarEventClientSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class LunarForecastHologramBlockEntityRenderer implements BlockEntityRenderer<LunarForecastHologramBlockEntity> {

    public LunarForecastHologramBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(LunarForecastHologramBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderCameraOrientedQuad(matrices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(StarAcademyMod.id("textures/block/hologram.png"), true)), stack -> {
            stack.translate(0.5, 2.2, 0.5);
            stack.scale(3.0f, 3.0f, 3.0f);
        });

        renderMoon(matrices, vertexConsumers, light, overlay);
        ClientWorld world = MinecraftClient.getInstance().world;

        if (world != null) {
            EnhancedCelestials.lunarForecastWorldData(world).ifPresent(data -> {
                RegistryEntry<LunarEvent> lunarEventRegistryEntry = data.nextScheduledLunarEvent();
                LunarEvent nextLunarEvent = lunarEventRegistryEntry.value();
                LunarEventClientSettings clientSettings = nextLunarEvent.getClientSettings();
                Identifier moonTexture = clientSettings.moonTextureLocation();
                matrices.push();
                matrices.translate(0.5, 2.6, 0.5);
                matrices.scale(0.025F, -0.025F, 0.025F);

                MinecraftClient client = MinecraftClient.getInstance();
                float cameraYaw = client.getEntityRenderDispatcher().camera.getYaw();
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));


                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                Text moonName = nextLunarEvent.getTextComponents().name().getComponent();
                float width = (float) (-textRenderer.getWidth(moonName) / 2);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));


                textRenderer.draw(moonName, width, 0, ColorHelper.Argb.getArgb(255, 255, 255, 255), false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, ColorHelper.Argb.getArgb(0, 0, 0, 0), light);
                textRenderer.draw("In 5 days...", width, MinecraftClient.getInstance().textRenderer.fontHeight + 1, ColorHelper.Argb.getArgb(255, 255, 255, 255), false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, ColorHelper.Argb.getArgb(0, 0, 0, 0), light);
                matrices.pop();
            });
        }
    }

    private static void renderMoon(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            EnhancedCelestials.lunarForecastWorldData(world).ifPresent(data -> {
                RegistryEntry<LunarEvent> lunarEventRegistryEntry = data.nextScheduledLunarEvent();
                LunarEventClientSettings clientSettings = lunarEventRegistryEntry.value().getClientSettings();
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
                renderCameraOrientedQuad(matrices, light, overlay, r, g, b, 0.8F, consumer, stack -> {
                    stack.translate(0.5, 3.15, 0.5);
                    stack.scale(1, 1, 1);
                });
            });
        }
    }

    private static void renderCameraOrientedQuad(MatrixStack matrices, int light, int overlay, float r, float g, float b, float a, VertexConsumer consumer, Consumer<MatrixStack> matricesTransforms) {
        matrices.push();
        matricesTransforms.accept(matrices);
        MinecraftClient client = MinecraftClient.getInstance();


        float cameraYaw = client.getEntityRenderDispatcher().camera.getYaw();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));


        MatrixStack.Entry peek = matrices.peek();
        Matrix4f positionMatrix = peek.getPositionMatrix();

        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-90.0f));
        consumer.vertex(positionMatrix, -0.5f, 0.0f, -0.5f).color(r, g, b, a).texture(0, 0).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
        consumer.vertex(positionMatrix, 0.5f, 0.0f, -0.5f).color(r, g, b, a).texture(1, 0).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
        consumer.vertex(positionMatrix, 0.5f, 0.0f, 0.5f).color(r, g, b, a).texture(1, 1).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
        consumer.vertex(positionMatrix, -0.5f, 0.0f, 0.5f).color(r, g, b, a).texture(0, 1).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
        matrices.pop();
    }
}