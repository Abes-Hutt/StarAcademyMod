package abeshutt.staracademy.outfit.core;

import abeshutt.staracademy.init.ModOutfits;
import abeshutt.staracademy.world.data.WardrobeData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class OutfitFeatureRenderer<
        M extends PlayerEntityModel<AbstractClientPlayerEntity>
        > extends FeatureRenderer<AbstractClientPlayerEntity, M> {

    public OutfitFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        WardrobeData.CLIENT.get(entity.getUuid()).ifPresent(entry -> {
            for(String id : entry.getEquipped()) {
                OutfitPiece outfit = ModOutfits.REGISTRY.get(id);
                if(outfit == null) continue;

                OutfitModel model = outfit.getModel();

                VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(
                        outfit.getTexture().getModel()
                ));

                this.getContextModel().copyBipedStateTo(model);
                model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            }
        });
    }

}
