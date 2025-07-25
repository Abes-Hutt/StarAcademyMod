package abeshutt.staracademy.compat.enhancedcelestials.block.entity.renderer;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.compat.enhancedcelestials.block.entity.LunarForecastHologramBlockEntity;
import dev.corgitaco.enhancedcelestials.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials.api.lunarevent.client.LunarEventClientSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class LunarForecastHologramBlockEntityRenderer implements BlockEntityRenderer<LunarForecastHologramBlockEntity> {

    public LunarForecastHologramBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(LunarForecastHologramBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        {
            matrices.push();

            MinecraftClient client = MinecraftClient.getInstance();
            matrices.translate(0.5, 2.2, 0.5);
            matrices.scale(3.0f, 3.0f, 3.0f);

            float cameraYaw = client.getEntityRenderDispatcher().camera.getYaw();
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));


            VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(StarAcademyMod.id("textures/block/hologram.png"), true));
            MatrixStack.Entry peek = matrices.peek();
            Matrix4f positionMatrix = peek.getPositionMatrix();

            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-90.0f));
            consumer.vertex(positionMatrix, -0.5f, 0.0f, -0.5f).color(255, 255, 255, 255).texture(0, 0).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
            consumer.vertex(positionMatrix, 0.5f, 0.0f, -0.5f).color(255, 255, 255, 255).texture(1, 0).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
            consumer.vertex(positionMatrix, 0.5f, 0.0f, 0.5f).color(255, 255, 255, 255).texture(1, 1).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
            consumer.vertex(positionMatrix, -0.5f, 0.0f, 0.5f).color(255, 255, 255, 255).texture(0, 1).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);


            matrices.pop();
        }
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            EnhancedCelestials.lunarForecastWorldData(world).ifPresent(data -> {
                RegistryEntry<LunarEvent> lunarEventRegistryEntry = data.nextScheduledLunarEvent();
                if (lunarEventRegistryEntry != null) {
                    matrices.push();

                    MinecraftClient client = MinecraftClient.getInstance();
                    matrices.translate(0.5, 3.15, 0.5);
                    matrices.scale(1, 1, 1);

                    float cameraYaw = client.getEntityRenderDispatcher().camera.getYaw();
                    matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));

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
                    MatrixStack.Entry peek = matrices.peek();
                    Matrix4f positionMatrix = peek.getPositionMatrix();

                    matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-90.0f));
                    float alpha = 0.8F;
                    consumer.vertex(positionMatrix, -0.5f, 0.0f, -0.5f).color(r, g, b, alpha).texture(0, 0).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
                    consumer.vertex(positionMatrix, 0.5f, 0.0f, -0.5f).color(r, g, b, alpha).texture(1, 0).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
                    consumer.vertex(positionMatrix, 0.5f, 0.0f, 0.5f).color(r, g, b, alpha).texture(1, 1).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);
                    consumer.vertex(positionMatrix, -0.5f, 0.0f, 0.5f).color(r, g, b, alpha).texture(0, 1).light(light).normal(peek, 0.0F, 1.0F, 0.0F).overlay(overlay);

                    matrices.pop();
                }
            });
        }
    }
}