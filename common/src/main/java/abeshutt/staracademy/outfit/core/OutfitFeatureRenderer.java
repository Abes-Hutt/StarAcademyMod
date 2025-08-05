package abeshutt.staracademy.outfit.core;

import abeshutt.staracademy.block.entity.renderer.DynamicOutfit;
import abeshutt.staracademy.client.OutfitManager;
import abeshutt.staracademy.util.ProxyAcademyClient;
import abeshutt.staracademy.world.data.WardrobeData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

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
        OutfitManager outfits = ProxyAcademyClient.get(MinecraftClient.getInstance()).getOutfits();
        Set<String> equipped = new HashSet<>(outfits.getEquipped(entity.getUuid()));

        WardrobeData.CLIENT.get(entity.getUuid()).ifPresent(entry -> {
            equipped.addAll(entry.getEquipped());
        });

        for(String id : equipped) {
            DynamicOutfit outfit = outfits.getRegistry().get(id);
            if(outfit == null) continue;
            Identifier texture = outfit.getTexture(this.slim);
            PlayerEntityModel<AbstractClientPlayerEntity> model = outfit.getModel(this.ctx, this.slim);
            this.getContextModel().copyBipedStateTo(model);
            VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(texture));
            model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }

}
