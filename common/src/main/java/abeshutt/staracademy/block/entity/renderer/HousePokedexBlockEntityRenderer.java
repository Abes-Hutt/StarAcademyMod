package abeshutt.staracademy.block.entity.renderer;

import abeshutt.staracademy.block.entity.HousePokedexBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class HousePokedexBlockEntityRenderer implements BlockEntityRenderer<HousePokedexBlockEntity> {

    public static final SpriteIdentifier BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            Identifier.ofVanilla("entity/enchanting_table_book"));
    private final BookModel book;

    public HousePokedexBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.book = new BookModel(context.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(HousePokedexBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, 0.75F, 0.5F);
        float g = (float)entity.ticks + tickDelta;
        matrices.translate(0.0F, 0.1F + MathHelper.sin(g * 0.1F) * 0.01F, 0.0F);

        double h = entity.bookRotation - entity.lastBookRotation;

        while(h >= Math.PI) {
            h -= 2.0D * Math.PI;
        }

        while(h < -3.1415927F) {
            h += 6.2831855F;
        }

        double k = entity.lastBookRotation + h * tickDelta;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)-k));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));
        float l = MathHelper.lerp(tickDelta, entity.pageAngle, entity.nextPageAngle);
        float m = MathHelper.fractionalPart(l + 0.25F) * 1.6F - 0.3F;
        float n = MathHelper.fractionalPart(l + 0.75F) * 1.6F - 0.3F;
        float o = MathHelper.lerp(tickDelta, entity.pageTurningSpeed, entity.nextPageTurningSpeed);
        this.book.setPageAngles(g, MathHelper.clamp(m, 0.0F, 1.0F), MathHelper.clamp(n, 0.0F, 1.0F), o);
        VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        this.book.renderBook(matrices, vertexConsumer, light, overlay, -1);
        matrices.pop();
    }

}
