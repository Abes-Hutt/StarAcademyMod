package abeshutt.staracademy.client;

import abeshutt.staracademy.StarAcademyMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class RenderUtil {


    public static void renderHologram(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay, float r, float g, float b) {
        renderCameraOrientedQuad(matrices, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, r, g, b, 1.0F, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(StarAcademyMod.id("textures/block/hologram.png"), true)), stack -> {
            stack.translate(0.5, 2.2, 0.5);
            stack.scale(3.0f, 3.0f, 3.0f);
        });
    }

    /**
     * @param r text red color component
     * @param g text green color component
     * @param b text blue color component
     * @param a text alpha color component
     * @param br background red color component
     * @param bg background green color component
     * @param bb background blue color component
     * @param ba background alpha color component
     */
    public static void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Text text, int r, int g, int b, int a, int br, int bg, int bb, int ba) {
        matrices.push();
        matrices.translate(0.5, 2.6, 0.5);
        matrices.scale(0.025F, -0.025F, 0.025F);
        MinecraftClient client = MinecraftClient.getInstance();
        float cameraYaw = client.getEntityRenderDispatcher().camera.getYaw();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float textWidth = (float) (-textRenderer.getWidth(text) / 2);


        float recipricalScale = 1;
        if (textWidth > 10) {
            recipricalScale = MathHelper.clampedLerp(1.0F, 0.1F, textWidth / 250F);
        }

//        matrices.scale(recipricalScale, recipricalScale, recipricalScale);
        textRenderer.draw(text, textWidth, 0, ColorHelper.Argb.getArgb(a, r, g, b), false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, ColorHelper.Argb.getArgb(ba, br, bg, bb), LightmapTextureManager.MAX_LIGHT_COORDINATE);
        matrices.pop();
    }

    public static void renderCameraOrientedQuad(MatrixStack matrices, int light, int overlay, float r, float g, float b, float a, VertexConsumer consumer, Consumer<MatrixStack> matricesTransforms) {
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
