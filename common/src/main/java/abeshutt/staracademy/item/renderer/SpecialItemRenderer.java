package abeshutt.staracademy.item.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;

public abstract class SpecialItemRenderer {

    public abstract void render(ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                                   VertexConsumerProvider vertexConsumers, int light, int overlay);

    public void renderModel(ModelIdentifier id, ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        this.renderModel(id, stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, () -> {});
    }

    public void renderModel(ModelIdentifier id, ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices,
                            VertexConsumerProvider vertexConsumers, int light, int overlay, Runnable action) {
        matrices.push();
        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(id);

        if(mode == ModelTransformationMode.GUI && !model.isSideLit()) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        matrices.translate(0.5F, 0.5F, 0.5F);
        model.getTransformation().getTransformation(mode).apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);

        action.run();

        boolean opaque;

        if(mode != ModelTransformationMode.GUI && !mode.isFirstPerson() && stack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem)stack.getItem()).getBlock();
            opaque = !(block instanceof TranslucentBlock) && !(block instanceof StainedGlassPaneBlock);
        } else {
            opaque = true;
        }

        RenderLayer renderLayer = RenderLayers.getItemLayer(stack, opaque);
        VertexConsumer vertexConsumer;

        if(opaque) {
            vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
        } else {
            vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
        }

        this.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);

        if(mode == ModelTransformationMode.GUI && !model.isSideLit()) {
            if(vertexConsumers instanceof VertexConsumerProvider.Immediate immediate) {
                RenderSystem.disableDepthTest();
                immediate.draw();
                RenderSystem.enableDepthTest();
            }

            DiffuseLighting.enableGuiDepthLighting();
        }

        matrices.pop();
    }

    private void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
        Random random = Random.create();

        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, direction, random), stack, light, overlay);
        }

        random.setSeed(42L);
        this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, null, random), stack, light, overlay);
    }

    private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
        MatrixStack.Entry entry = matrices.peek();

        for(BakedQuad bakedQuad : quads) {
            int color = -1;

            if(!stack.isEmpty() && bakedQuad.hasColor()) {
                color = MinecraftClient.getInstance().itemColors.getColor(stack, bakedQuad.getColorIndex());
            }

            float alpha = (float)ColorHelper.Argb.getAlpha(color) / 255.0F;
            float red = (float)ColorHelper.Argb.getRed(color) / 255.0F;
            float green = (float)ColorHelper.Argb.getGreen(color) / 255.0F;
            float blue = (float)ColorHelper.Argb.getBlue(color) / 255.0F;
            vertices.quad(entry, bakedQuad, red, green, blue, alpha, light, overlay);
        }
    }

}
