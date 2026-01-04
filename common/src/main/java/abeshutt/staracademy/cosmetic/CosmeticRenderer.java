package abeshutt.staracademy.cosmetic;

import abeshutt.staracademy.StarAcademyMod;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimation;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class CosmeticRenderer {

    public static void render(Identifier modelId, Identifier animationId, String animationName, Identifier texture,
                              PosableState state, MatrixStack matrices, VertexConsumerProvider provider, int light,
                              CosmeticsResources resources, PlayerEntityModel<AbstractClientPlayerEntity> referenceModel,
                              boolean slim) {
        RenderContext context = new RenderContext();
        ModelPart bakedModel = resources.getBakedModel(modelId).orElse(null);
        PosableModel posableModel = resources.getPosableModel(modelId).orElse(null);

        if (bakedModel == null || posableModel == null) {
            return;
        }

        posableModel.setContext(context);
        posableModel.setDefault();

        BedrockAnimation animation = resources.getAnimation(animationId)
                .map(group -> group.getAnimations().get(animationName))
                .orElse(null);

        if (animation != null) {
            animation.run(posableModel, state, state.getAnimationSeconds(), 0.0f, 0.0f,
                    state.getAnimationSeconds() * 20.0f, 1.0f);
        }

        RenderLayer layer = getRenderLayer(texture, false, false);
        VertexConsumer buffer = ItemRenderer.getDirectItemGlintConsumer(provider, layer, false, false);

        matrices.push();
        String prefix = slim ? "slim" : "wide";
        bakedModel.children.put("right_arm", bakedModel.children.get(prefix + "_right_arm"));
        bakedModel.children.put("right_sleeve", bakedModel.children.get(prefix + "_right_sleeve"));
        bakedModel.children.put("left_arm", bakedModel.children.get(prefix + "_left_arm"));
        bakedModel.children.put("left_sleeve", bakedModel.children.get(prefix + "_left_sleeve"));
        // No cape yet.
        bakedModel.children.put("cloak", new ModelPart(new ArrayList<>(), new HashMap<>()));
        bakedModel.children.put("ear", new ModelPart(new ArrayList<>(), new HashMap<>()));

        PlayerEntityModel<AbstractClientPlayerEntity> fakePlayerModel = new PlayerEntityModel<>(bakedModel, slim);
        referenceModel.copyBipedStateTo(fakePlayerModel);
        fakePlayerModel.render(matrices, buffer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        matrices.pop();
    }

    private static RenderLayer getRenderLayer(Identifier texture, boolean emissive, boolean translucent) {
        if (!emissive && !translucent) {
            return RenderLayer.getEntityCutout(texture);
        } else if (!emissive) {
            return RenderLayer.getEntityTranslucent(texture);
        }

        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                .program(RenderPhase.ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM)
                .texture(new RenderPhase.Texture(texture, false, false))
                .transparency(translucent ? RenderPhase.TRANSLUCENT_TRANSPARENCY : RenderPhase.NO_TRANSPARENCY)
                .cull(RenderPhase.ENABLE_CULLING)
                .writeMaskState(RenderPhase.ALL_MASK)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .build(false);

        return RenderLayer.of("cosmetics_entity_layer",
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS,
                256, true, translucent, multiPhaseParameters);
    }

    public static void testRender(AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> contextModel,
                                  float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean slim) {
        CosmeticPosableState state = new CosmeticPosableState(entity);
        state.updatePartialTicks(tickDelta);

        CosmeticRenderer.render(StarAcademyMod.id("cosmetics/models/moltres_wings"),
                StarAcademyMod.id("cosmetics/animations/moltres_wings"),
                "animation.moltres_wings.idle",
                StarAcademyMod.id("cosmetics/textures/moltres_wings.png"), state, matrices,
                vertexConsumers, light, StarAcademyMod.RESOURCES, contextModel, slim);
    }

}
