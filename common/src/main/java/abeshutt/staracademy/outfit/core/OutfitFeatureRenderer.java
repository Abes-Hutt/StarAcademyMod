package abeshutt.staracademy.outfit.core;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.cosmetic.CosmeticPosableState;
import abeshutt.staracademy.cosmetic.CosmeticRenderer;
import abeshutt.staracademy.live.CosmeticsManager;
import abeshutt.staracademy.proxy.ProxyAcademyClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class OutfitFeatureRenderer<M extends PlayerEntityModel<AbstractClientPlayerEntity>> extends FeatureRenderer<AbstractClientPlayerEntity, M> {

    private final EntityRendererFactory.Context ctx;
    private final boolean slim;

    public OutfitFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, M> renderer,
                                 EntityRendererFactory.Context ctx, boolean slim) {
        super(renderer);
        this.ctx = ctx;
        this.slim = slim;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta,
                       float animationProgress, float headYaw, float headPitch) {
        CosmeticsManager outfits = ProxyAcademyClient.get(MinecraftClient.getInstance()).getCosmetics();

        outfits.getEquipped(entity.getUuid()).forEach((slot, cosmeticId) -> {
            if(cosmeticId == null) return;
            CosmeticPosableState state = new CosmeticPosableState(entity);
            state.updatePartialTicks(tickDelta);

            StarAcademyMod.RESOURCES.getCosmetic(cosmeticId).ifPresent(cosmetic -> {
                CosmeticRenderer.render(cosmetic.getModel(), cosmetic.getAnimation(),
                        "idle", cosmetic.getTexture(), state, matrices,
                        vertexConsumers, light, StarAcademyMod.RESOURCES,
                        this.getContextModel(), this.slim);
            });
        });
    }

}
