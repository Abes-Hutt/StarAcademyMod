package abeshutt.staracademy.entity.renderer;

import abeshutt.staracademy.entity.ShootingStarEntity;
import abeshutt.staracademy.entity.model.ShootingStarModel;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ShootingStarRenderer extends EntityRenderer<ShootingStarEntity> {

    private final ShootingStarModel model;

    public ShootingStarRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new ShootingStarModel();
    }

    @Override
    public void render(ShootingStarEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        matrices.push();
        matrices.scale(5.0F, 5.0F, 5.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(ShootingStarModel.TEXTURE));
        this.model.render(matrices, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        matrices.pop();

        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
    }

    @Override
    public Identifier getTexture(ShootingStarEntity entity) {
        return ShootingStarModel.TEXTURE;
    }

    @Override
    public boolean shouldRender(ShootingStarEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

}
