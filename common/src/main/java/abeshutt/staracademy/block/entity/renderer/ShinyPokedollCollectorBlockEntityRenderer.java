package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.block.entity.ShinyPokedollCollectorBlockEntity;
import abeshutt.staracademy.client.RenderUtil;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class ShinyPokedollCollectorBlockEntityRenderer implements BlockEntityRenderer<ShinyPokedollCollectorBlockEntity> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    public ShinyPokedollCollectorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(ShinyPokedollCollectorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RenderUtil.renderHologram(matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, 82.0F / 255.0F, 57.0F / 255F, 141F / 255F);
        RenderUtil.renderText(matrices, vertexConsumers, Text.literal(DECIMAL_FORMAT.format(entity.getProgress() * 100F) + "%"), 255, 255, 255, 255, 0, 0, 0, 0);

    }
}
