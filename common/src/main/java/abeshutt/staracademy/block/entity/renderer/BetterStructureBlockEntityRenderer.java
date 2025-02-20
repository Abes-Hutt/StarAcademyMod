package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.block.entity.BetterStructureBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class BetterStructureBlockEntityRenderer implements BlockEntityRenderer<BetterStructureBlockEntity> {
    public BetterStructureBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(
            BetterStructureBlockEntity structureBlockBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j
    ) {
        if (MinecraftClient.getInstance().player.isCreativeLevelTwoOp() || MinecraftClient.getInstance().player.isSpectator()) {
            BlockPos blockPos = structureBlockBlockEntity.getOffset();
            Vec3i vec3i = structureBlockBlockEntity.getSize();
            if (vec3i.getX() >= 1 && vec3i.getY() >= 1 && vec3i.getZ() >= 1) {
                if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.getMode() == StructureBlockMode.LOAD) {
                    double d = blockPos.getX();
                    double e = blockPos.getZ();
                    double g = blockPos.getY();
                    double h = g + (double)vec3i.getY();
                    double k;
                    double l;
                    switch (structureBlockBlockEntity.getMirror()) {
                        case LEFT_RIGHT:
                            k = vec3i.getX();
                            l = -vec3i.getZ();
                            break;
                        case FRONT_BACK:
                            k = -vec3i.getX();
                            l = vec3i.getZ();
                            break;
                        default:
                            k = vec3i.getX();
                            l = vec3i.getZ();
                    }

                    double m;
                    double n;
                    double o;
                    double p;
                    switch (structureBlockBlockEntity.getRotation()) {
                        case CLOCKWISE_90:
                            m = l < 0.0 ? d : d + 1.0;
                            n = k < 0.0 ? e + 1.0 : e;
                            o = m - l;
                            p = n + k;
                            break;
                        case CLOCKWISE_180:
                            m = k < 0.0 ? d : d + 1.0;
                            n = l < 0.0 ? e : e + 1.0;
                            o = m - k;
                            p = n - l;
                            break;
                        case COUNTERCLOCKWISE_90:
                            m = l < 0.0 ? d + 1.0 : d;
                            n = k < 0.0 ? e : e + 1.0;
                            o = m + l;
                            p = n - k;
                            break;
                        default:
                            m = k < 0.0 ? d + 1.0 : d;
                            n = l < 0.0 ? e + 1.0 : e;
                            o = m + k;
                            p = n + l;
                    }

                    float q = 1.0F;
                    float r = 0.9F;
                    float s = 0.5F;
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
                    if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE || structureBlockBlockEntity.shouldShowBoundingBox()) {
                        WorldRenderer.drawBox(matrixStack, vertexConsumer, m, g, n, o, h, p, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
                    }

                    if (structureBlockBlockEntity.getMode() == StructureBlockMode.SAVE && structureBlockBlockEntity.shouldShowAir()) {
                        this.renderInvisibleBlocks(structureBlockBlockEntity, vertexConsumer, blockPos, matrixStack);
                    }
                }
            }
        }
    }

    private void renderInvisibleBlocks(BetterStructureBlockEntity entity, VertexConsumer vertices, BlockPos pos, MatrixStack matrices) {
        BlockView blockView = entity.getWorld();
        BlockPos blockPos = entity.getPos();
        BlockPos blockPos2 = blockPos.add(pos);

        for (BlockPos blockPos3 : BlockPos.iterate(blockPos2, blockPos2.add(entity.getSize()).add(-1, -1, -1))) {
            BlockState blockState = blockView.getBlockState(blockPos3);
            boolean bl = blockState.isAir();
            boolean bl2 = blockState.isOf(Blocks.STRUCTURE_VOID);
            boolean bl3 = blockState.isOf(Blocks.BARRIER);
            boolean bl4 = blockState.isOf(Blocks.LIGHT);
            boolean bl5 = bl2 || bl3 || bl4;
            if (bl || bl5) {
                float f = bl ? 0.05F : 0.0F;
                double d = (float)(blockPos3.getX() - blockPos.getX()) + 0.45F - f;
                double e = (float)(blockPos3.getY() - blockPos.getY()) + 0.45F - f;
                double g = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.45F - f;
                double h = (float)(blockPos3.getX() - blockPos.getX()) + 0.55F + f;
                double i = (float)(blockPos3.getY() - blockPos.getY()) + 0.55F + f;
                double j = (float)(blockPos3.getZ() - blockPos.getZ()) + 0.55F + f;
                if (bl) {
                    WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
                } else if (bl2) {
                    WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
                } else if (bl3) {
                    WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
                } else if (bl4) {
                    WorldRenderer.drawBox(matrices, vertices, d, e, g, h, i, j, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
                }
            }
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(BetterStructureBlockEntity structure) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 2000000;
    }
}