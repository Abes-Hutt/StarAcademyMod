package abeshutt.staracademy.entity.renderer;

import abeshutt.staracademy.entity.model.StarBadgeBeltModel;
import abeshutt.staracademy.world.data.StarBadgeData;
import abeshutt.staracademy.world.inventory.BaseInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class StarBadgeBeltRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private final StarBadgeBeltModel model;

    public StarBadgeBeltRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.model = new StarBadgeBeltModel();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(player.isInvisible()) return;

        matrices.push();
        matrices.translate(0.0D, -0.07D, 0.0D);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(StarBadgeBeltModel.TEXTURE));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));

        BaseInventory inventory = StarBadgeData.CLIENT.getInventories().get(player.getUuid());

        if(inventory != null) {
            Vec3d topCenter = new Vec3d(-0.02D, -0.68D, 0.19D);
            Vec3d topSpread = new Vec3d(0.1D, -0.041D, 0.0D);
            Vec3d bottomCenter = new Vec3d(0.015D, -0.74D, 0.19D);
            Vec3d bottomSpread = new Vec3d(0.1D, -0.041D, 0.0D);

            List<Vec3d> ordered = new ArrayList<>();

            for(int i = 0; i < inventory.size(); i++) {
               if(i % 2 == 0) {
                   ordered.add(topCenter.add(topSpread.multiply((i >> 1) - 2)));
               } else {
                   ordered.add(bottomCenter.add(bottomSpread.multiply((i >> 1) - 2)));
               }
            }

            for(int i = 0; i < inventory.size(); i++) {
                if(inventory.getStack(i).isEmpty()) {
                    continue;
                }

                matrices.push();
                Vec3d offset = ordered.get(i);
                matrices.translate(offset.x, offset.y, offset.z);
                matrices.scale(0.25F, 0.25F, 0.25F);

                MinecraftClient.getInstance().getItemRenderer().renderItem(inventory.getStack(i),
                        ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, 0);
                matrices.pop();
            }
        }

        matrices.pop();
    }

}
